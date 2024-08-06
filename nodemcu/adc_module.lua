local modname = ...
local id, sda, scl = 0, 6, 5
local _adc = nil
local channels = {ads1115.SINGLE_0,ads1115.SINGLE_1,ads1115.SINGLE_2,ads1115.SINGLE_3}
local activeChannel = -1

local function setActiveChannel(ch)
    if ch == activeChannel then 
        return
    end
    
    if ch == nil or ch > 3 then
        ch = 1
    end
    _adc:setting(ads1115.GAIN_4_096V,ads1115.DR_860SPS,channels[ch],ads1115.CONTINUOUS) 
    activeChannel = ch
     
end
 

local function initModule()
        --package.loaded[modname] = nil
        
        i2c.setup(id, sda, scl, i2c.SLOW)
        ads1115.reset()
        _adc = ads1115.ads1115(id, ads1115.ADDR_GND)
        setActiveChannel(1)
        print("ADS inited at id" .. id .. " sda " .. sda .. " scl " ..scl)
end

local function readActiveChannel()
    volt,volt_dec,raw,sign = _adc:read()   
    return {volt,raw}
end
local function readAdsValue(channel)
    setActiveChannel(channel)
    return readActiveChannel()
end

local function readSingleShot(ch,callback)
        _adc:setting(ads1115.GAIN_4_096V,ads1115.DR_860SPS,channels[ch],ads1115.SINGLE_SHOT) 
        _adc:startread(function(volt, volt_dec, raw, sign) 
            callback({volt,raw})
        end)
end
return {
    init = initModule,
    readChannel = readAdsValue, 
    readSingleShot=readSingleShot,
    readActiveChannel = readActiveChannel,
    setChannel = setActiveChannel
}
