package com.ecommerce.vmall.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import com.ecommerce.vmall.pojo.SubmitOrderItem;

@Mapper
public interface OrderDao {

	//插入自营订单表 
	@Insert("INSERT INTO self_order(oid,shid,firstname,lastname,telephone,address,status"
			+ ",created)"
			+ "VALUES(#{id},#{shid},#{firstname},#{lastname},#{telephone},#{address},"
			+ "#{status},#{created})")
	int selfInsert(SubmitOrderItem soi);
	
	//插入属于第三方的订单表
	@Insert("INSERT INTO third_order(oid,sid,firstname,lastname,telephone,address,"
			+ "status,created)"
			+ "VALUES(#{id},#{sid},#{firstname},#{lastname},#{telephone},#{address},"
			+ "#{status},#{created})")
	int thirdInsert(SubmitOrderItem soi);
	
	//读取自营订单
    SubmitOrderItem selectOrderByOid(int oid);	
	
}
