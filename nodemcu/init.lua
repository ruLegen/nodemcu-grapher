print("IIIIIIIIIIIIIIIIIINIT")
node.LFS.utils()
package.loaded["http_module"] = nil


local app = node.LFS.get("grapher_app")()
local fm = node.LFS.get("file_manager")()
local http_m = node.LFS.get("http_module")()
local wifi_m = node.LFS.get("wifi_conf")()
local adc_m = node.LFS.get("adc_module")()

fm.init()
http_m.init(5683) -- init http server to 5683 port
wifi_m.init("test AP","12345678")   --init wifi Access Point with provided ssid and password
adc_m.init()
collectgarbage("collect")
app.init(fm,http_m,adc_m)
node.setcpufreq(node.CPU160MHZ)
