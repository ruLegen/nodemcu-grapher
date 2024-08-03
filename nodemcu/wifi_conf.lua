local modname = ...

package.loaded[modname] = nil

wifi.setmode(wifi.SOFTAP)


return {
    init = function(ssid,pass)
        package.loaded[modname] = nil
        ap_cfg=
        {
            ssid=ssid,
            pwd=pass,
            auth = wifi.WPA2_PSK
        }
        wifi.ap.config(ap_cfg)  
        print("Wifi initialized " .. ssid .. "   " .. pass)
    end
}


--[[ 
ipconfig=   
{
    ip="192.168.1.2",
    netmask="255.255.255.0",    
    gateway="192.168.1.1"
}
wifi.ap.setip(ipconfig)
wifi.ap.dhcp.start()
--]]
