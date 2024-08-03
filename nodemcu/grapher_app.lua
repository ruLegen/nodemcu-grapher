local coap_m = nil
local fm = nil
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


local function initApp(fileManager,coapServer)
    package.loaded[modname] = nil
    coap_m = coapServer
    fm = fileManager
 
    coap_m.register("test",test_handler)
    coap_m.register("cc",cc_handler)
  
    print("App inited")
end




return {
    init = initApp
}
