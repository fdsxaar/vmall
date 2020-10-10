package com.ecommerce.testdata.pojo;

import lombok.Data;

//代表数据库中orderlist表的行
@Data
public class OrderItem {
  
	private int id;    //产品id
	private int is;    //0自营大件，1自营小件，2第三方货物
	private int sid;   //店铺id
	private int num;   //购买的数量
}
