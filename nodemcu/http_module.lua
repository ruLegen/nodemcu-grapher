local modname = ...
----package.loaded[modname] = nil
local httpserver = node.LFS.get("httpserver")()
local http = nil
local cmd_handlers = {} -- str -> arrayOf<function>

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

local function http_response(res,code,msg)
    res:send(nil, code)
    res:send_header("Connection", "close")
    res:send(msg)
    res:finish()
end

function http_handler(req, res)
    local url = req.url
    if url == nil then
        url=""
    end
    local payload =string.sub(url, 2) 
    if payload == nil then
        http_response(res,500,"cmd null")
        return 
    end
    local parsedPayload = split(payload,"_")
    local splitLen = #parsedPayload
    if splitLen == 0 then
       http_response(res,500,"no response")
       return 
    end
    local filteredPayload = map(parsedPayload, 
        function(val)
            local noEndNewLine = val:gsub('\n*$',"")
            local res =  noEndNewLine:gsub("\r*$","")
            return res
        end
    )
    filteredPayload = filter(filteredPayload, 
        function(val)
            return val ~= "" and val ~= "\r\n" and #val > 0
        end
    )
    if #filteredPayload == 0 then
        http_response(res,500,"no response after filter")
        return 
    end
    local cmd = filteredPayload[1]
    local handlers = cmd_handlers[cmd]
    if handlers == nil or #handlers == 0 then
        http_response(res,500,"no handler registered")
        return 
    end
    
    local respond = ""
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
    local returnVal = respond:gsub('\n*$',"")
    http_response(res,200,returnVal)
end
return {
    init = function(port) 
        -- --package.loaded[modname] = nil
        --cs:listen(port)
        http = httpserver.createServer(port, http_handler)
        print("Listening CoAP " .. port)
    end,
    register = registerCmdHandler,
}

