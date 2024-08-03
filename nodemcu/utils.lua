    function print_t(object)
        for k,v in pairs(object) do
            print(k,v)
        end
    end    
    function bool_to_number(value)
        if value == nil then return value end
       return value and 1 or 0
end
