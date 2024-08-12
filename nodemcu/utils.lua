function rebootLFS(name)
    name = name or "luac.out"
    node.LFS.reload(name)
end

function table_to_str(object)
    if object == nil then
        return "{}"
    end
    res = ""
    for k, v in pairs(object) do
        res = res .. k .. "=" .. v .. ";"
    end
    return res
end
function array_to_str(object, delimetr)
    if object == nil then
        return "{}"
    end
    if delimetr == nil then
        delimetr = ";"
    end
    res = ""
    for k, v in ipairs(object) do
        res = res .. v .. delimetr
    end
    return res
end
local random = math.random
function uuid()
    local template = 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'
    return string.gsub(template, '[xy]', function(c)
        local v = (c == 'x') and random(0, 0xf) or random(8, 0xb)
        return string.format('%x', v)
    end)
end
function getOrNull(arr, index)
    if arr == nil or #arr < index then
        return nil
    end
    return arr[index]
end

function removeDuplicates(arr)
    if arr == nil then
        return {}
    end

    local hash = {}
    local res = {}

    for _, v in ipairs(arr) do
        if (not hash[v]) then
            res[#res + 1] = v -- you could print here instead of saving to result table if you wanted
            hash[v] = true
        end
    end
    return res
end

function print_t(object)
    for k, v in pairs(object) do
        print(k, v)
    end
end
function bool_to_number(value)
    if value == nil then
        return value
    end
    return value and 1 or 0
end
function str_to_int(val)
    res = tonumber(val)
    if res == nil then
        res = 0
    end
    return res
end
function split(s, delimiter)
    local result = {};
    for match in (s .. delimiter):gmatch("(.-)" .. delimiter) do
        table.insert(result, match);
    end
    return result;
end

function filter(arr, functor)
    local res = {}
    for i, val in ipairs(arr) do
        if functor(val) then
            table.insert(res, val)
        end
    end
    return res
end
function map(arr, functor)
    local res = {}
    for _, val in ipairs(arr) do
        res[#res + 1] = functor(val)
    end
    return res
end
local function sendfile(filename)
    local offset = 0
    local function send()
        local f = file.open(filename, "r")
        if f and f:seek("set", offset) then
            local r = f:read()
            f:close()
            if r then
                offset = offset + #r
                return r, send
            end
        end
        -- implicitly returns nil, nil and falls out of the stream
    end
    return send, function()
        return offset
    end
end
function sendFileViaSocket(file, socket,fifosocket)
    print("try send file via socket" .. file)
    
    local ssend = fifosocket.wrap(socket) 
    local dosf, getsent = sendfile(file)
    ssend(dosf)
    ssend = nil
    socket:on("sent", nil)
    socket:close()
end

function sendStringViaSocket(str,socket,fifosocket)
    local ssend = fifosocket.wrap(socket) 
    ssend(str)
    ssend = nil
    socket:on("sent", nil)
    socket:close()
end