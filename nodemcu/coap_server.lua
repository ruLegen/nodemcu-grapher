local modname = ...
----package.loaded[modname] = nil

local cs=coap.Server()
local cmd_handlers = {} -- str -> arrayOf<function>

function cmd_handler(payload)
    
    if payload == nil then
        return "cmd null"
    end
    parsedPayload = split(payload," ")
    splitLen = #parsedPayload
    if splitLen == 0 then
        return "no response"
    end

    filteredPayload = filter(parsedPayload, 
        function(val)
            return val ~= "" and val ~= "\r\n" and #val > 0
        end
    )
    if #filteredPayload == 0 then
        return "no response after filter"
    end
    cmd = filteredPayload[1]
    handlers = cmd_handlers[cmd]

    if handlers == nil or #handlers == 0 then
        return "no handler registered"  
    end
    
    respond = ""
    for index,handler in ipairs(handlers) do
        status,err = pcall(function() 
            local handler_res = handler(filteredPayload)
            if handler_res ~= nil then
                respond = respond .. handler_res .. "\n"
            end
        end)
        if not status then
            print(err)
        end
    end
    if #respond == 0 then
        respond= "OK"
    end
    return respond:gsub('\n*$',"")
end

cs:func("cmd_handler") -- post coap://192.168.18.103:5683/v1/f/cmd will call myfun


local function registerCmdHandler(cmd,handler)
    if cmd == nil or cmd == "" or #cmd == 0 or handler == nil then
        return false
    end 
    
    handlers = {}
    if cmd_handlers[cmd] ~= nil then
        handlers = cmd_handlers[cmd]
    end
    table.insert(handlers,handler)
    cmd_handlers[cmd] = handlers
    return true
end

return {
    init = function(port) 
        -- --package.loaded[modname] = nil
        cs:listen(port)
        print("Listening CoAP " .. port)
    end,
    register = registerCmdHandler,
}

