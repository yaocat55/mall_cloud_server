-- 冻结库存：原子执行 DECR available + INCR frozen
-- KEYS[1] = inv:available:{productId}
-- KEYS[2] = inv:frozen:{productId}
-- ARGV[1] = 扣减数量（负数传给 INCRBY）

local qty = -tonumber(ARGV[1])  -- INCRBY 需要负数
local avail = redis.call('INCRBY', KEYS[1], qty)

if avail < 0 then
    -- 库存不足，补回
    redis.call('INCRBY', KEYS[1], -qty)
    return -1
end

-- 库存充足，增加冻结
redis.call('INCRBY', KEYS[2], -qty)

-- 返回扣减前的可用库存（avail - qty = 扣减前的值）
return avail - qty
