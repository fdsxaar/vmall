package com.ecommerce.vmall.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;



@Mapper
public interface OrderItemDao {

	
	
	@Insert("INSERT INTO self_order_item(oid,pid,num)"
			+ "VALUES(#{oid},#{pid},#{num})")
	int selfInsert(long oid, int pid, int num); //插入订单
	
	@Insert("INSERT INTO third_order_item(oid,pid,num)"
			+ "VALUES(#{oid},#{pid},#{num})")
	int thirdInsert(long oid, int pid, int num); //插入订单
}
