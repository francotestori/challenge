-- Rate Limiter LUA script
local key = KEYS[1]
local requests = tonumber(redis.call('GET', key) or '-1')
local max_requests = tonumber(ARGV[1])
local expiry = tonumber(ARGV[2])

-- check if either first request or request under max_requests
if (requests == -1) or (requests < max_requests) then
    -- we increment counter
    redis.call('INCR', key)

    -- we expire only first key entry
    if(requests == -1) then
        redis.call('EXPIRE', key, expiry)
    end

    return false
else
    return true
end