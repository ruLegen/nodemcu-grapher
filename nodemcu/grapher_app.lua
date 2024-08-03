local coap_m = nil
local fm = nil
local modname = ...

local function initApp(fileManager,coapServer)
    package.loaded[modname] = nil
    coap_m = coapServer
    fm = fileManager
    print("App inited")
end

return {
    init = initApp
}