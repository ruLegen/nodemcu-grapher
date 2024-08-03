dofile("utils.lua")
package.loaded["coap_server"] = nil

local fm = require("file_manager")
local coap_m = require("coap_server")
local wifi_m = require("wifi_conf")
local app = require("grapher_app")

fm.init()
coap_m.init(5683) -- init coap server to 5683 port
wifi_m.init("test AP","12345678")   --init wifi Access Point with provided ssid and password
app.init(fm,coap)