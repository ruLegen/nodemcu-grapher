local modname = ...

local function initModule()
        math.randomseed(tmr.now())

        package.loaded[modname] = nil
        print("ADS inited")
end

local function readAdsValue(channel)
    return math.random(1024)
end

return {
    init = initModule,
    readAnalog = readAdsValue,
    
}