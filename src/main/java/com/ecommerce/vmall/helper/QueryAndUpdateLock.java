package com.ecommerce.vmall.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 提交订单后，在查询并redis时获取一个锁，所定该库房中的某个产品的数量。
 * 采用分段锁策略，比如有10个库房，那么就实例化10个
 */


public class QueryAndUpdateLock {

	public static Map<Integer,ReentrantLock> lockMap = new HashMap<>();;
	static{
		//假定有10个库房
		for(int n = 34;n>0;n--){
			//库房的键
			lockMap.put(n,new ReentrantLock());
		}
	}	
}
