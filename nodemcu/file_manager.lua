local modname = ...


local function init_files()
    --package.loaded[modname] = nil
    print("File Manager inited")
end
local function getFreeSpace()
    remaining, used, total=file.fsinfo()
    return remaining
end

local function createFileForStream(name)
    return file.open(name, "w+")
end

local function closeFile(stream)
    pcall(function() stream:close() end)
end
local function getTimeStamp(time)
    return time.year * 31536000
            + time.mon * 2630000
            + time.day * 86400 
            + time.hour * 3600
            + time.min * 60
            + time.sec
end
local function getSampleFiles()
    local allFiles = file.list()
    local samples = {}
  
    for k,v in pairs(file.list()) do
        if string.find(k,".samples") ~= nil then
            local stat = file.stat(k)
            local timestamp = getTimeStamp(stat.time)
            samples[#samples+1] = {k,v,timestamp}
        end
    end
    return samples
end
local function removeSampleFile(filename)
    pcall(function()
        file.remove(filename)
    end)
end
return {
    init = init_files,
    getFreeSpace = getFreeSpace,  -- in bytes
    createFileForStream = createFileForStream,
    closeFile = closeFile,
    getSampleFiles = getSampleFiles,
    removeSampleFile = removeSampleFile,
}
