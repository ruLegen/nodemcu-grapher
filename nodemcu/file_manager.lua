local modname = ...


local function init_files()
    package.loaded[modname] = nil
    print("File Manager inited")
end
local function getFreeSpace()
    remaining, used, total=file.fsinfo()
    return remaining
end
return {
    init = init_files,
    getFreeSpace = getFreeSpace,  -- in bytes
}
