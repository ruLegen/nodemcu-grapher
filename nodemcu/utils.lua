function table_to_str(object) 
    if object == nil then
        return "{}"
    end
    res = ""
    for k,v in pairs(object) do
        res = res .. k .. "=" .. v .. ";"
    end
    return res
end
local random = math.random
function uuid()
    local template ='xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'
    return string.gsub(template, '[xy]', function (c)
        local v = (c == 'x') and random(0, 0xf) or random(8, 0xb)
        return string.format('%x', v)
    end)
end

function print_t(object)
    for k,v in pairs(object) do
        print(k,v)
    end
end
function bool_to_number(value)
    if value == nil then return value end
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
    for match in (s..delimiter):gmatch("(.-)"..delimiter) do
        table.insert(result, match);
    end
    return result;
end

function filter(arr,functor)
    local res = {}
    for i,val in ipairs(arr) do
        if functor(val) then
            table.insert(res, val)
        end
    end
    return res
end
function map(arr,functor)
    local res = {}
    for i,val in ipairs(arr) do
         table.insert(res, functor(val))
    end
    return res
end