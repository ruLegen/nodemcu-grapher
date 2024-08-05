local coap_s = nil
local fm = nil
local adc_m = nil
local isAdcThreadEnabled = false
local lastChannelValues = {}
local modname = ...
local cohelper = require("cohelper")
local activeWriteStreams = {}       -- guid -> {guid,filename,channel,status}

local function channel_handler(payload)
    local strArray = map(lastChannelValues,function(sample) 
        return table_to_str(sample)
    end)
    local res = table.concat(strArray,"\n")
    return res
end

local function main_adc_thread(coroutineScope)
    print("Main Adc thread STARTED")
    local startTime = tmr.now()
 
    while isAdcThreadEnabled do
        cohelper.delay(1000,nil,coroutineScope)
       -- stat,err = pcall(function() 
        --[[for i in pairs({1,2,3,4}) do
           readRes =  adc_m.readChannel(i)
           res = res .. i .. " " .. readRes[1]/1000 .. ";"
        end
                print(res)
        --]]
        --end)

       --  Single shot
       local channel_values = {}
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
       lastChannelValues = channel_values
        --r = map(channel_values,function(val) return table_to_str(val) end)
        --print_t(r)

 
        
    end
    --print(error)
   
    print("Main Adc thread STOPED")
end
local function start_adc_thread()
    print("Free memory " .. fm.getFreeSpace()/1024.0 .. " kb")
    if fm.getFreeSpace() < 200 then
        isAdcThreadEnabled = false
        print("No free memory left")
        return
    end
    isAdcThreadEnabled = true
    
    startTimer = tmr.create()
    startTimer:register(5000, tmr.ALARM_SINGLE, 
        function() 
            cohelper.exec(main_adc_thread)
         end)
    startTimer:start()
end
local function initApp(fileManager,coapServer,adsModule)
    package.loaded[modname] = nil
    
    coap_s = coapServer
    fm = fileManager
    adc_m = adsModule 
    math.randomseed(node.random(1024))

    coap_s.register("test",test_handler)
    coap_s.register("cc",cc_handler)
    coap_s.register("space",function(payload)
        return fm.getFreeSpace() 
    end)

    coap_s.register("channel",channel_handler)
    coap_s.register("adc_off",function(payload)
         isAdcThreadEnabled = false
    end)
    start_adc_thread()
    print("App inited")
end




return {
    init = initApp
}
