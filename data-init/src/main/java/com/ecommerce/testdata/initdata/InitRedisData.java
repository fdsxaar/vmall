package com.ecommerce.testdata.initdata;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import com.ecommerce.testdata.pojo.SelfIty;
import com.ecommerce.testdata.pojo.Storehouse;
import com.ecommerce.testdata.pojo.ThirdProduct;
import com.ecommerce.testdata.service.ProductService;
import com.ecommerce.testdata.service.StorehouseService;
import com.ecommerce.testdata.service.ThirdProductService;

@Component
public class InitRedisData {
	@Autowired
	RedisTemplate<String,Object> redisTemplate;
	
	 @Autowired
	StringRedisTemplate stringRedisTemplate;
	 
	@Resource(name="stringRedisTemplate")
    private ValueOperations<String, String> valueOpt;
	
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, Object> hashOpt;
	

    @Autowired
    ProductService productService;
    @Autowired
    StorehouseService storehouseService;
    @Autowired
    ThirdProductService thirdProductService;
    
    public void deleteOldKeys() {
    	if(redisTemplate.hasKey("orders")) {
    		redisTemplate.delete("orders");
    	}
    	
    	if(redisTemplate.hasKey("orders_zset")) {
    		redisTemplate.delete("orders_zset");
    	}
    	
    	if(redisTemplate.hasKey("orders_hashtable")) {
    		redisTemplate.delete("orders_hashtable");
    	}
    	
    	if(redisTemplate.hasKey("orders_view")) {
    		redisTemplate.delete("orders_view");
    	}
    }
    
    //将产品库存读到Redis
    public void writeInventory() {
    	
    	List<SelfIty> sl ;
    	String key;
    	String hashKey;
    	for(int i=1; i<=34; i++) {
    		 sl = productService.findAll(i);
    		 //String.valueOf(shid)+"invent",库房在Redis中的键 
    		 key = String.valueOf(i)+"invent";
    		 for(SelfIty si : sl) {
    			 hashKey = String.valueOf(si.getPid());
    			 hashOpt.put(key, hashKey, si.getIty());
    		 }
    	}
    }
    
    //读取库房信息到Redis
    public void writeStorehouseInfo() {
    	List<Storehouse>shl = storehouseService.select();
        for(Storehouse sh : shl) {
        	hashOpt.put("storehouseinfo", String.valueOf(sh.getShid()), sh.getNshid());
        	
        }
    }
    
    //插入第三方商品
    public void writeThirdGood() {
    	List<ThirdProduct> tpl = thirdProductService.selectAll();
    	for(ThirdProduct tp : tpl) {
        	int id = tp.getId();
        	int ity = tp.getInventory();
        	hashOpt.put("thirdinvent", String.valueOf(id), ity);
        	ity = (int)hashOpt.get("thirdinvent", String.valueOf(id));

    	}
    	
    }
    
    //设置订单递增键
    public void initOrderId() {
    	valueOpt.set("order_id", "0");
    }
    
}
