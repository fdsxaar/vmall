package com.ecommerce.vmall.helper;

import java.time.Instant;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.ecommerce.vmall.pojo.SubmitOrderItem;

/**
 * 周期性扫描orders_zset,找出过期的订单，写入数据库
*/
@Component
public class CronOrders {
	
	@Autowired
	RedisTemplate<String,Object> redisTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, Object> hashOpt;

	@Resource(name="stringRedisTemplate")
	private ZSetOperations<String,String> zsetOpt;
	
	@Resource(name="stringRedisTemplate")
	private HashOperations<String, String, String> ohashOpt;
	
	@Autowired
	OrderManage orderManage;
	
	@Scheduled(fixedRate=120000, initialDelay=30000) //每两分钟执行 
	public void cronOrders() {
		long t  = Instant.now().getEpochSecond(); //用于和Redis中的数据比较
		long expire = t - 180; //1800之前创建的订单为过期订单
		Set<String>expiredSet = zsetOpt.rangeByScore("orders_zset", 0, expire, 0, 100);//返回100个
		for(String e : expiredSet) {
			//rangeByScore返回的是[min,max]区间的值，是闭区间，值包括min和max,上面
			//用0做min的值，由于orders_zset中score的值都是大于0的,所以返回值中没有和0相等；但是
			//在orderCheckAndUpdate.lua中,订单处理由等号，为了防止max可能等于刚刚处理的订单，要排除
			if(Long.parseLong(e)!=t) {
				SubmitOrderItem soi = (SubmitOrderItem) hashOpt.get("orders", e);
				soi.setStatus(OrderStatus.EXPIRE.toString()); //更改订单状态
				orderManage.writeExpiredOrderToMySQL(soi); //写入数据库 
				orderManage.orderRollback(soi); //订单过期后，回滚redis中的数据
				ohashOpt.put("orders_view", e, String.valueOf(OrderStatus.EXPIRE)); //更新订单视图
				zsetOpt.remove("orders_zset", e);
				ohashOpt.delete("orders_hashtable", e);
				System.out.println("清理订单 "+ e );
		    }
		}
		//log
		System.out.println("在时间 "+t +"执行一次过期订单清理工作");
	}
} 
