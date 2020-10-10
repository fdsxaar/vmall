package com.ecommerce.vmall.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
/**
 * 用于处理订单的辅助类，存储函数执行过程中产生的数据
 *
 */
@Data
public class SubmitOrderMiddle {

	//订单oid到订单订单项orderItem的映射
	private Map<String,List<OrderItem>> selfSuccessOrderMap; 
	private Map<String,List<OrderItem>> thirdSuccessOrderMap;
	//将库存不足的商品的存入列表
	private List<OrderItem> lackOrderList ;
	private List<String> notEnoughStockList;
	//未成功删除的订单
	private List<Long> failedTODeleteOrder;
	
    public SubmitOrderMiddle(){
    	this.selfSuccessOrderMap = new HashMap<>();
    	this.thirdSuccessOrderMap = new HashMap<>();
        this.lackOrderList = new ArrayList<>();
        this.failedTODeleteOrder = new ArrayList<>();
        this.notEnoughStockList = new ArrayList<>();
    }
}
