function print_t(object)
    for k,v in pairs(object) do
        print(k,v)
    end
end
function bool_to_number(value)
    if value == nil then return value end
    return value and 1 or 0
end
function split(s, delimiter)
    result = {};
    for match in (s..delimiter):gmatch("(.-)"..delimiter) do
        table.insert(result, match);
    end
    return result;
end

function map(arr,functor)
    res = {}
    for i,val in ipairs(arr) do
        if functor(val) then
            table.insert(res, val)
        end
    end
    return res
end