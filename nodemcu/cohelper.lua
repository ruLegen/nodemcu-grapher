--[[  A coroutine Helper   T. Ellison,  June 2019

This version of couroutine helper demonstrates the use of corouting within
NodeMCU execution to split structured Lua code into smaller tasks

]]
--luacheck: read globals node

local modname = ...

local function taskYieldFactory(co)
  local post = node.task.post
  return function(nCBs,priority)  -- upval: co,post
    if priority == nil then
    priority = node.task.MEDIUM_PRIORITY
    end
    post(priority,function () -- upval: co, nCBs
      coroutine.resume(co, nCBs or 0)
    end)
    return coroutine.yield() + 1
  end
end
local function taskDelay(ms,nCBs, coroutineScope)
    if coroutineScope == nil then
        print("coroutine scope is null ...")
        return
    end
    start = tmr.now()
    now = start
    delayTimeoutUs = ms * 1000
    while now < start + delayTimeoutUs do
        --print("now" .. now .. " end at " .. start + delayTimeoutUs )
        coroutineScope(nCBs,node.task.LOW_PRIORITY)
        now = tmr.now()
    end
end
return { 
    delay = taskDelay,
    exec = function(func, ...) -- upval: modname
            package.loaded[modname] = nil
            local co = coroutine.create(func)
            return coroutine.resume(co, taskYieldFactory(co), ... )
        end
 }


