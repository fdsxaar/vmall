package com.ecommerce.vmall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.vmall.dao.OrderDao;
import com.ecommerce.vmall.pojo.SubmitOrderItem;

@Service
public class OrderService {

	@Autowired
	OrderDao orderDao;
	
	
	
	//插入自营订单
	public int selfInsert(SubmitOrderItem soi) {
		return orderDao.selfInsert( soi);
	}
	
	//插入第三方订单
	public int thirdInsert(SubmitOrderItem soi) {
		return orderDao.thirdInsert( soi);
	}
}
