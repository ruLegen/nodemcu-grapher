--[[  A coroutine Helper   T. Ellison,  June 2019

This version of couroutine helper demonstrates the use of corouting within
NodeMCU execution to split structured Lua code into smaller tasks

]]
--luacheck: read globals node

local modname = ...

local function taskYieldFactory(co)
  local post = node.task.post
  local function yield(nCBs,priority)  -- upval: co,post
    if priority == nil then
    priority = node.task.MEDIUM_PRIORITY
    end
    post(priority,function () -- upval: co, nCBs
      coroutine.resume(co, nCBs or 0)
    end)
    return coroutine.yield() + 1
  end
  local function waitAsyncFunction(fn,nCBs) -- dont call this form current coroutine
    local function continue()
        post(function () -- upval: co, nCBs
            coroutine.resume(co, nCBs or 0)
        end)
    end
    fn(continue)
    coroutine.yield()
  end
  return {
    breakAndRepost = yield,
    waitAsyncFunction = waitAsyncFunction
  }
end
local function taskDelay(ms,nCBs, coroutineScope)
    if coroutineScope == nil then
        print("coroutine scope is null ...")
        return
    end
    local started = false
    coroutineScope.waitAsyncFunction(function(continue) 
        started = tmr.create():alarm(ms, tmr.ALARM_SINGLE, function()
            continue()
        end)
        if not started then
            continue()
        end
    end,nCBs)
    
    if not started then
        print("Semi")
        -- semi active waiting
        start = tmr.now()
        now = start
        delayTimeoutUs = ms * 1000
        while now < start + delayTimeoutUs do
            --print("now" .. now .. " end at " .. start + delayTimeoutUs )
            coroutineScope.breakAndRepost(nCBs,node.task.LOW_PRIORITY)
            now = tmr.now()
        end
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


