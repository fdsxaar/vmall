-- stockcheckandupdate.lua
local result=''          --返回结果
local stocknum           --存货数量
--local status           --状态变量
local stocktable = {}    --存储商品到更新后剩余库存的映射
local ordernum
for j=1, #ARGV do
    stocknum = redis.call('HGET',KEYS[1],KEYS[j+1]) + 0  --转换成数字 
    ordernum = tonumber(ARGV[j])   --订单需要的量
    if stocknum >= ordernum then
        stocktable[KEYS[j+1]] = stocknum - ordernum
    else
        result = result ..KEYS[j+1]..':'
    end 
end

if result == '' then --表示没有缺货，库存足够 
    for k,v in pairs(stocktable) do
        redis.call('HSET',KEYS[1],k,v)
        --if status == 0         --更改订单成功
        --then 
            --
        --else                   --有别的程序删除了该商品的field,应该在应用层施加约束
        --    result = 'f'
        --    return result 

        --end 
    end 
    result = 't'              --true
    return result
else                         --缺货，返回缺货的商品
    return result
end 


