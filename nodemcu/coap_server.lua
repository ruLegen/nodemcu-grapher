local modname = ...

package.loaded[modname] = nil
local cs=coap.Server()
function c_myfun(payload)
  print(payload)
  respond = "hello"
  return respond
end

cs:func("c_myfun") -- post coap://192.168.18.103:5683/v1/f/myfun will call myfun

return {
    init = function(port) 
        package.loaded[modname] = nil
        cs:listen(port)
        print("Listening CoAP " .. port)
    end
}

