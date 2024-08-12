local coap_s = nil
local fm = nil
local adc_m = nil
local isAdcThreadEnabled = false
local lastChannelValues = {}
local modname = ...
local cohelper = node.LFS.get("cohelper")()
local fifosocket = node.LFS.get("fifosock")()

local activeWriteStreams = {} -- guid -> {guid,filename,filestream,channels,status,createdTime}

local STREAM_ACTIVE = 1
local STREAM_CLOSED = 0

local function createActiveStream(guid, filename, filestream, channels, status, createdTime)
    return {
        guid = guid,
        filename = filename,
        filestream = filestream,
        channels = channels,
        status = status,
        createdTime = createdTime
    }
end
local function getFileStatus(fname)
    if fname == nil then
        return STREAM_CLOSED
    end
    local splited = split(fname,"%.")
    local fileGuid = getOrNull(splited,1)
    fileGuid = tonumber(fileGuid)
    if fileGuid == nil then
        return STREAM_CLOSED
    end
    local stream = activeWriteStreams[fileGuid]
    if stream ~= nil then
        return stream.status
    else
        return STREAM_CLOSED
    end
end

local function channel_handler(payload)
    local strArray = map(lastChannelValues, function(sample)
        return table_to_str(sample)
    end)
    local res = table.concat(strArray, "\n")
    return res
end
local function stream_hanlder(payload)
    collectgarbage("collect")
    local cmd = getOrNull(payload, 2)
    if cmd == nil then
        return "bad cmd"
    end
    if cmd == "start" then
        local channels = {unpack(payload, 3)}
        local int_channels = map(channels, function(ch)
            return tonumber(ch)
        end)
        local notdup_int_channels = filter(int_channels, function(ch)
            return ch ~= nil
        end)
        local start_channels = removeDuplicates(notdup_int_channels)
        if #start_channels == 0 then
            return "channels not specified"
        end

        local guid = node.random(99999) * node.random(99999)
        local fileName = guid .. ".samples"

        collectgarbage("collect")
        local createdFile = fm.createFileForStream(fileName)

        if createdFile == nil then
            return "cannot create stream"
        end

        local createdStream = createActiveStream(guid, fileName, createdFile, start_channels, STREAM_ACTIVE, tmr.now())
        activeWriteStreams[guid] = createdStream

        return guid
    elseif cmd == "stop" then
        local stopguid = getOrNull(payload, 3)
        if stopguid == nil then
            return "guid not specified"
        end
        local key = tonumber(stopguid)
        local stop_stream = activeWriteStreams[key]
        if stop_stream == nil then
            return "cannot find specified stream"
        end
        activeWriteStreams[key] = nil
        fm.closeFile(stop_stream.filestream)

        return stopguid
    end

    return "unknown cmd"
end
local function files_handler(payload)
    collectgarbage("collect")
    local cmd = getOrNull(payload, 2)
    if cmd == nil then
        return "bad cmd"
    end

    if cmd == "list" then
        local files = fm.getSampleFiles()
        for _, f in ipairs(files) do
            local fileName = getOrNull(f,1)
            if fileName ~= nil then
                f[#f+1] = getFileStatus(fileName)
            end
        end
        local strArray = map(files, function(f)
            return array_to_str(f, ";")
        end)
        local res = table.concat(strArray, "@")
        if #res == 0 then
            return ""
        end
        return res
    elseif cmd == "read" then
        local fileName = getOrNull(payload,3)
        if fileName == nil then
            return "specify file name or ALL"
        end
        if not file.exists(fileName) then
            return "file not exist"
        end
        local sv = net.createServer(net.TCP, 30)
        if not sv then
            return "file exist but cannot send it"
        end
        local port = node.random(1025,65000)
        sv:listen(port,function(socket)
            sendFileViaSocket(fileName,socket,fifosocket)
        end)        
        return port

    elseif cmd == "remove" then
        local fileName = getOrNull(payload,3)
        if fileName == nil then
            return "specify file name or ALL"
        end
        local filesToDelete = {}
        if fileName == "ALL" then
            local allFiles = map(fm.getSampleFiles(),function(f)
                return f[1]     --get only name
            end)
            local openedFiles = {}
            for k, v in pairs(activeWriteStreams) do
                openedFiles[v.filename] = true
            end
            filesToDelete = filter(allFiles,function(f)     --remove frome list current opened files
                return openedFiles[f] ~= true
            end)
        else    
            filesToDelete = {fileName}
        end
        for _, v in ipairs(filesToDelete) do
            fm.removeSampleFile(v)
        end
        return "removed " .. #filesToDelete
    end
    return "unknown cmd"

end
local function main_adc_thread(coroutineScope)
    print("Main Adc thread STARTED")
    local startTime = tmr.now()

    while isAdcThreadEnabled do

        local channel_values = {}
        cohelper.delay(1000, nil, coroutineScope)

        -- stat,err = pcall(function() 
        for i in pairs({1, 2, 3, 4}) do
            local readRes = adc_m.readChannel(i)
            local sampleTime = tmr.now() -- uS
            local volts = readRes[1] / 1000.0 -- mv to V
            channel_values[#channel_values + 1] = {
                time = sampleTime,
                value = volts,
                channel = i
            }
        end
        -- ]]
        -- end)
        --[[
       --  Single shot
       --workaround; direct variable assignmenet in deep coroutine doesnt work for some reason
       local function insert(val) table.insert(channel_values,val) end   
       
        for i in pairs({1,2,3,4}) do
            coroutineScope.waitAsyncFunction(function(continue) 
                adc_m.readSingleShot(i,function(readRes)  
                    local sampleTime = tmr.now()  -- uS
                    local volts = readRes[1]/1000.0 -- mv to V
                    insert({time = sampleTime, value = volts, channel=i})
                    continue();
                end)
            end)
        end
        --]]
        lastChannelValues = channel_values

        for k, writeStream in pairs(activeWriteStreams) do
            if writeStream.status == STREAM_ACTIVE then
                local writechannels = writeStream.channels
                for _, ch in ipairs(writechannels) do
                    local channelValue = channel_values[ch]
                    if channelValue ~= nil then
                        local timeSinceRecord = (channelValue.time - writeStream.createdTime)/1000000       --Seconds
                        local writeValue = struct.pack("<ffi",timeSinceRecord,channelValue.value,channelValue.channel)     -- time value channel
                        stat,err = pcall(function() 
                            local stream = writeStream.filestream
                            stream.write(writeValue)
                            stream.flush()
                        end)
                        -- if err then
                        --     print(err)
                        -- end
                    end
                end 
            end
        end
    end
    print("Main Adc thread STOPED")
end
local function start_adc_thread()
    print("Free memory  " .. fm.getFreeSpace() / 1024.0 .. " kb")
    if fm.getFreeSpace() < 200 then
        isAdcThreadEnabled = false
        print("No free memory left; remove some files and reboot")
        return
    end
    isAdcThreadEnabled = true

    startTimer = tmr.create()
    startTimer:register(5000, tmr.ALARM_SINGLE, function()
        cohelper.exec(main_adc_thread)
    end)
    startTimer:start()
end
local function initApp(fileManager, coapServer, adsModule)
    -- package.loaded[modname] = nil
    collectgarbage("collect")
    coap_s = coapServer
    fm = fileManager
    adc_m = adsModule
    math.randomseed(node.random(1024))

    coap_s.register("space", function(payload)
        return fm.getFreeSpace()
    end)

    coap_s.register("channel", channel_handler)
    coap_s.register("stream", stream_hanlder)
    coap_s.register("files", files_handler)
    coap_s.register("heartbeat", function(payload)
        return "OK"
    end)

    coap_s.register("adc_off", function(payload)
        isAdcThreadEnabled = false
    end)
    start_adc_thread()
    print("App inited")
end

return {
    init = initApp
}
