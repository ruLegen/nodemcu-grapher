print("IIIIIIIIIIIIIIIIIINIT")
node.LFS.utils()
package.loaded["coap_server"] = nil


local app = node.LFS.get("grapher_app")()
local fm = node.LFS.get("file_manager")()
local coap_m = node.LFS.get("coap_server")()
local wifi_m = node.LFS.get("wifi_conf")()
local adc_m = node.LFS.get("adc_module")()

fm.init()
coap_m.init(5683) -- init coap server to 5683 port
wifi_m.init("test AP","12345678")   --init wifi Access Point with provided ssid and password
adc_m.init()
collectgarbage("collect")
app.init(fm,coap_m,adc_m)
node.setcpufreq(node.CPU160MHZ)
