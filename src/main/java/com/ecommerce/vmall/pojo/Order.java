package com.ecommerce.vmall.pojo;

import java.util.List;

import lombok.Data;

@Data
public class Order {

	private CustomerInfo ci;                //收货人信息
	private int shid;              //与收货人相匹配的库房storeHouseId的Id
	private List<OrderItem> list;  //订购的所有货物
	
}
