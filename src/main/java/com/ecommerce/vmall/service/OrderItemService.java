package com.ecommerce.vmall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.vmall.dao.OrderItemDao;

@Service
public class OrderItemService {
  
	@Autowired
	OrderItemDao oderItemDao;
   
	
	
	//插入自营订单项
	public int selfInsert(long oid, int pid, int num) {
		return oderItemDao.selfInsert(oid, pid, num);
	}
	
	//插入第三方订单项
	public int thirdInsert(long oid, int pid, int num) {
		return oderItemDao.thirdInsert(oid, pid, num);
	}
}
