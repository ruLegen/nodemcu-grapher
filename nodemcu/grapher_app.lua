local coap_s = nil
local fm = nil
local adc_m = nil
local isAdcThreadEnabled = false


local modname = ...
local cohelper = require("cohelper")

local function test_handler(payload)
    print("TEST")
    print_t(payload)
end
local function cc_handler(payload)
    print("CCC")
    return 123
end
local function channel_handler(payload)
    channelNum = 0
    if payload == nil or #payload == 1 then
        return "invalid command"
    end
    
    if #payload > 1 then
        channelNum = str_to_int(payload[2])
    end

    return adc_m.readAnalog(channelNum)
end

local function main_adc_thread(taskYield)
    print("Main Adc thread STARTED")
    while isAdcThreadEnabled do
        cohelper.delay(100,nil,taskYield)
    end
    print("Main Adc thread STOPED")
end
local function start_adc_thread()
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
    ads_m = adsModule 
    
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
