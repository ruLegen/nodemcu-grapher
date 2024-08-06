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
local function getSampleFiles()
    local allFiles = file.list()
    local samples = {}
  
    for k,v in pairs(file.list()) do
        if string.find(k,".samples") ~= nil then
            samples[#samples+1] = {k,v}
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
