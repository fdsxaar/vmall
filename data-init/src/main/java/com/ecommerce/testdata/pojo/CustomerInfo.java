package com.ecommerce.testdata.pojo;

import lombok.Data;

@Data
public class CustomerInfo {
    
	private String firstname;     //收货人姓
	private String lastname;      //收货人名
	private String address;       //地址
	private String telephone;        //收货人电话
}
