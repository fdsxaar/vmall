package com.ecommerce.vmall.webcontroller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;
import org.springframework.stereotype.Controller;

import com.ecommerce.vmall.service.OrderItemService;
import com.ecommerce.vmall.service.OrderService;
import com.ecommerce.vmall.service.ProductService;

@Controller
public class PayController {
 
	@Autowired
	RedisTemplate<String,Object> redisTemplate;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Resource(name="redisTemplate")
	private HashOperations<String, String, Object> hashOpt;
	
	
	@Autowired 
	ProductService productService;
	@Autowired 
	OrderItemService orderItemService;
	@Autowired 
	OrderService orderService;
	
	public RedisScript<Boolean> script() {
		  DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();

		  ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("/scripts/orderCheckAndUpdate.lua"));
		  redisScript.setScriptSource(scriptSource);
		  redisScript.setResultType(Boolean.class);
		  return redisScript;
		}
	
	public Boolean orderCheckAndUpdate(String orderId, String t) {
		  List<String> keys = new ArrayList<>();
		  keys.add("orders_hashtable"); //KEYS[1]
		  keys.add("orders_zset");	//KEYS[2]
		  keys.add("orders_view"); //KEYS[3]
		  keys.add(orderId); //KEYS[4]

	    return stringRedisTemplate.execute(script(),
	    		keys, t);
	  }
	/**
	 * TODO:支付功能比较复杂，这里是简单逻辑
	 * 用lua脚本实现简单的付款逻辑
	 * @param orderId 订单的id
	 * @return 如果没超时，则付款成功；否则，失败
	 */
	boolean payController(String orderId) {
		String t  = String.valueOf(Instant.now().getEpochSecond()); //用于和Redis中的数据比较
		boolean b = orderCheckAndUpdate(orderId,t).booleanValue();
		return b;
	}
}
