package com.ecommerce.vmall.webcontroller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


import com.ecommerce.vmall.helper.GlobalVarEncloseClass;
import com.ecommerce.vmall.helper.OrderManage;
import com.ecommerce.vmall.pojo.CustomerInfo;
import com.ecommerce.vmall.pojo.Order;
import com.ecommerce.vmall.pojo.OrderItem;
import com.ecommerce.vmall.pojo.SubmitOrderMiddle;
import com.ecommerce.vmall.service.OrderItemService;
import com.ecommerce.vmall.service.ProductService;


@RestController
public class SubmitOrderController {
	@Autowired
	OrderItemService orderItemService;
    @Autowired
    ProductService productService;
    
    @Autowired
    OrderManage orderManage ; ;
    
    /**
     * 直营电商的商品分为两种，电商自营和第三方店铺售卖，要区别对待。
     * 
     * 顾客购买的自营商品可以分为两种，大件商品和小件商品。大件商品，比如冰箱、
	 * 电视机、空调，这类商品是直营电商的主打商品，由于质量大，考虑到运输成本，
	 * 每个区域/省会一般都有库房，专门存储此类商品，节省远程配送费用。
	 * 
	 * 当顾客提交订单时，直接查询与顾客选择的送货地址对应的仓库中，看看是否有货，
	 * 如果没货的话，要等生产厂家的本地经销商补货；而小件商品，西北、西南地区的
	 * 仓库可能没有存货，系统要从别的库房调，北上广深等经济发达地区的本地仓库有货，
	 * 
	 * 所以，订单的处理逻辑分为如何处理大件商品和如何处理小件商品。
	 * 一、大件商品:
	 *   查询与订单对应的本地库房是否有该大件商品。如果有，则直接购买；如果没有，返回此请求
	 *   的响应体，提醒顾客缺货，修改订单。
	 * 二、小件商品:
	 *   查询与订单对应的本地库房是否有该小件商品。如果有，则直接购买；如果没有，先计算出离
	 *   当前库房最近的库房，查询商品的库存情况，决定是否能够购买。
	 *   TODO:有时候，某一种小件商品集中存储于一个库房，当购买时，除了这个库房外，其他的库房都缺货，
	 *   在查询时库存情况时，如果不增加一层处理逻辑，容易造成栈增长和频繁访问Redis数据库
	 * 
	 * TODO:对于自营商品，在实际的业务中，不管订单中的自营商品中是否由别的库房调配，只生成一个订单，
	 * 但是，由于商品是由多个仓库发出，可能有多个物流信息。可以通过增加一个列表list到som参数中处理这种情况，
	 * 在商品调配时生成物流信息，加入该列表。
	 */
    @PostMapping("/submitorder/")
	public void submitOrder(@RequestBody Order order) {
    	GlobalVarEncloseClass.orderAccept.incrementAndGet();
		System.out.println(GlobalVarEncloseClass.orderAccept.get());
    	//TODO:大量的new操作内存开销很大,JVM要选择合适的垃圾收集器 。
    	//考察实际的业务模式，进行优化
    	int shid = order.getShid();   //与订单地址应的库房id
    	CustomerInfo ci = order.getCi(); //送货信息
    	SubmitOrderMiddle som = new SubmitOrderMiddle();

 	    List<OrderItem> orderItemList = order.getList(); //订单中的所有商品
     	List<OrderItem> selfHeavyGoods = new ArrayList<>(); //自营大件
    	List<OrderItem> selfSmallGoods = new ArrayList<>(); //自营小件
    	List<OrderItem> thirdGoods = new ArrayList<>(); //第三方店铺销售的商品
    	
    	orderManage.divideOrder(orderItemList,selfHeavyGoods,selfSmallGoods,thirdGoods); //分拆订单 
		
    	//先处理自营商品
    	if(!selfHeavyGoods.isEmpty()) { //有大件
		    if(!selfSmallGoods.isEmpty()) { //有小件
		    	List<OrderItem> selfSmallIn = new ArrayList<>(); //在本仓库的小件
		        List<OrderItem> selfSmallOut = new ArrayList<>(); //不在本仓库的小件

		        orderManage.divideSelfSmallOrder(shid,selfSmallGoods,selfSmallIn,selfSmallOut);
		    	//大件和所有小件在同一个库房
		    	if(selfSmallOut.size() ==0) {
		    		//同时处理大件与小件
		    		if(selfHeavyGoods.addAll(selfSmallIn)) {
			    	    orderManage.checkAndUpdateInventory(shid,-1,selfHeavyGoods,som);
		    		}else {
		    			assert 1<0: "addAll失败";
		    		}
		    	}else{ //大件和小件不在同一个库房
		    		if(selfSmallIn.size()==0) { //所有小件从别处调配
		    			//先处理大件
		    			orderManage.checkAndUpdateInventory(shid,-1,selfHeavyGoods,som);
		    			//从别的仓库调配小件
		    			orderManage.dispatchSelfSmallOrder(shid,selfSmallOut,som);
		    		}else { //部分小件商品从别处调配
		    			//处理大件和小件
		    			selfHeavyGoods.addAll(selfSmallIn); //有返回值
		    		    orderManage.checkAndUpdateInventory(shid,-1,selfHeavyGoods,som);
		    			orderManage.dispatchSelfSmallOrder(shid,selfSmallOut,som);
		    		}
		    	}
		    }else{//无小件
		    	//直接处理大件
		    	orderManage.checkAndUpdateInventory(shid,-1,selfHeavyGoods,som);
		    }
		}else {//无大件商品
			List<OrderItem> selfSmallIn = new ArrayList<>(); //在本仓库的小件
	        List<OrderItem> selfSmallOut = new ArrayList<>(); //不在本仓库的小件
	        orderManage.divideSelfSmallOrder(shid,selfSmallGoods,selfSmallIn,selfSmallOut);
			//所有小件在同一个库房
	    	if(selfSmallOut.size()==0) {
	    		//处理小件
		    	orderManage.checkAndUpdateInventory(shid,-1,selfSmallIn,som);		
	    	}else{ //小件不在同一个库房
	    		if(selfSmallIn.size() == 0) { //所有小件从别处调配
	    			orderManage.dispatchSelfSmallOrder(shid,selfSmallOut,som);
	    		}else { //部分小件商品从别处调配
	    			//先处理本仓库的小件
			    	orderManage.checkAndUpdateInventory(shid,-1,selfSmallIn,som);		
	    			//从别的仓库调配
		    	    orderManage.dispatchSelfSmallOrder(shid,selfSmallOut,som);
	    		}
	    	}
		}
    	
		//System.out.println("处理三方货物");

    	//处理第三方店铺出售的货物
    	if(thirdGoods.size()==0){
    		//System.out.println("所有订单处理完成");
    	}else {
    		//店铺shop到订单项的映射
    		Map<String,List<OrderItem>> stoo = new HashMap<>();
    		orderManage.divideGoodsByShopId(thirdGoods,stoo);
    		//public void forEach(BiConsumer<? super K,? super V> action)
    		Set<String> keySet = stoo.keySet();
    		//处理每个店铺的订单
            for(String s : keySet) {
            	List<OrderItem> oo = stoo.get(s);
            	int sid = Integer.parseInt(s);
            	orderManage.checkAndUpdateInventory(-1,sid, oo,som);
            }
    	}
    	
    	//检测缺货状态，查看数据是否需要回滚
    	List<OrderItem> ll = som.getLackOrderList();
    	if(ll.size()==0) {//无缺货
    		orderManage.genOrder(ci,som);	
    	}else {
    		//System.out.println("缺少的货物为"+som.getLackOrderList());
    		orderManage.handleLackOfInventory(som);
    	}
    	
    }

        @GetMapping("/submitorder/")
        public String submitOrder() {
			return "hello";
        }
}

