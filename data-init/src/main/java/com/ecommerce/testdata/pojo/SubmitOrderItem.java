package com.ecommerce.testdata.pojo;

import java.util.List;


import lombok.Data;

@Data
public class SubmitOrderItem {

    private long id=-1;            //订单id  
    private int shid = -1;            //库房id,由该库房配货
    private int sid;             //店铺shop的id
    private int status;          //订单配送状态
    private long created;         //创建事件
	private String firstname;     //收货人姓
	private String lastname;      //收货人名
	private String address;       //地址
	private String telephone;        //收货人电话
	private List<OrderItem> plist;    //订单项
	
}
