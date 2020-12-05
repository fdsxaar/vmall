package com.ecommerce.vmall.helper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;

import com.ecommerce.vmall.pojo.CustomerInfo;
import com.ecommerce.vmall.pojo.OrderItem;
import com.ecommerce.vmall.pojo.SubmitOrderItem;
import com.ecommerce.vmall.pojo.SubmitOrderMiddle;
import com.ecommerce.vmall.service.OrderItemService;
import com.ecommerce.vmall.service.OrderService;
import com.ecommerce.vmall.service.ProductService;

@Component 
public class OrderManage{
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	@Autowired
	RedisTemplate<String,Object> redisTemplate;
	@Autowired
	RedisScript<String> stockCheckAndUpdate;
	
	@Resource(name="redisTemplate")
	private HashOperations<String, String, Object> hashOpt;
	
	//用stringRedisTemplate
	@Resource(name="stringRedisTemplate")
	private HashOperations<String, String, String> stringHashOpt;
	
	@Resource(name="stringRedisTemplate")
    private ValueOperations<String, String> valueOpt;
	
	//zset操作要用stringRedisTemplate,不然用Lua脚本执行时,会用Json版本的字符串，如 "\"a\""
	@Resource(name="stringRedisTemplate")
	private ZSetOperations<String,String> zsetOpt;
	
	@Resource(name="redisTemplate")
	private SetOperations<String,Object> setOpt;
	
	@Autowired
	OrderService orderService;
	
	@Autowired
	ProductService productService;
	@Autowired 
	OrderService orderListService;
	@Autowired
	OrderItemService orderItemService;
	
	/**
	 * 分拆订单。0代表自营大件商品，1代表自营小件商品，2代表第三方出售的商品
	 * @param oo 原来的订单中的货物
	 * @param shg 自营大件商品(selfHeavyGoods)
	 * @param ssg 自营小件商品(selfSmallGoods)
	 * @param tg  第三方店铺出售商品(thirdGoods)
	 */
	
	public void divideOrder(List<OrderItem>oo,List<OrderItem> shg,
			List<OrderItem>ssg,List<OrderItem>tg) {
		for(OrderItem oi : oo) {
			if(oi.getIs() == 0) {
				shg.add(oi);
			}else if(oi.getIs() == 1){
				ssg.add(oi);
			}else if(oi.getIs() == 2){
				tg.add(oi);
			}else {
				//网页数据有误
			}
		}
	}
	
	/**
	 * 根据仓库的id(shid)分拆自营小件商品（ssg)，在本仓库的存入in列表，
	 * 不在本仓库的存入out
	 * @param shid 订单对应的仓库(storehouse)id
	 * @param ssg  自营小件商品(self small goods)
	 * @param in   在本仓库的商品结果列表in
	 * @param out  不在本仓库的商品结果的列表out
	 */
	
	public void divideSelfSmallOrder(int shid,List<OrderItem>ssg,List<OrderItem> in,
			List<OrderItem>out) {
		String sshid = String.valueOf(shid)+"invent";
		
		for(OrderItem oi : ssg) {
			String id =  String.valueOf(oi.getId());
			//判断OrderItem代表的产品是否在库房sshid中
			if(hashOpt.hasKey(sshid, id)) {
				in.add(oi);
			}else{
				out.add(oi);			
			}
		}
	}

	//找到离当前仓库SHID最近的仓库;在Redis中的键为storehouseinfo
	public int findNearestStoreHouse(int shid) {
		Object o = hashOpt.get("storehouseinfo", String.valueOf(shid));
		
		return (int)o;
	}

	public void setStorehouseId(int shid, List<OrderItem> oo) {
	   oo.forEach(oi -> oi.setSid(shid));	
	}
	
	//生成订单
	public void genOrder(CustomerInfo ci,SubmitOrderMiddle som) {
		if(som.getSelfSuccessOrderMap().size()!=0) {
			genSelfOrder(ci,som);
		}
		
		if(som.getThirdSuccessOrderMap().size()!=0) {
			genThirdOrder(ci,som);
		}
	}
	
	/**
	 * 生成自营订单
	 * @param ci
	 * @param som
	 */
	public void genSelfOrder(CustomerInfo ci,SubmitOrderMiddle som) {
		SubmitOrderItem soi = new SubmitOrderItem();
		long t; //订单创建时间 
		long orderId;
		String sorderId; //订单id
		Map<String,List<OrderItem>> gsom = som.getSelfSuccessOrderMap();
		Set<String> keySet = gsom.keySet();
	    for(String k : keySet) {
	        orderId = valueOpt.increment("order_id");
			sorderId = String.valueOf(orderId);//订单的唯一id
			soi.setId(orderId);
			soi.setShid(Integer.parseInt(k));
			soi.setFirstname(ci.getFirstname());
			soi.setLastname(ci.getLastname());
			soi.setAddress(ci.getLastname());
			soi.setTelephone(ci.getTelephone());
			soi.setPlist(gsom.get(k));
			soi.setStatus(OrderStatus.NOTPAYED.toString());
	        t = Instant.now().getEpochSecond();
		    soi.setCreated(t); //设置创建时间
		    //生成订单，插入到Redis
		    hashOpt.put("orders", sorderId, soi);
		
		    stringHashOpt.put("orders_view", sorderId, OrderStatus.NOTPAYED.toString());
	        //orders_hashtable与orders_zset协同管理订单
		    stringHashOpt.put("orders_hashtable",sorderId,String.valueOf(t));
		    zsetOpt.add("orders_zset", sorderId, t);
		    //orders_owner映射订单属于哪个库房，或哪个第三方店铺
		    //hashOpt.put("orders_owner", String.valueOf(orderId), sid);
	    }
	}
	
	/**
	 * 生成第三方订单
	 * @param ci
	 * @param som
	 */
	public void genThirdOrder(CustomerInfo ci,SubmitOrderMiddle som) {
		SubmitOrderItem soi = new SubmitOrderItem();
		long t; //订单创建时间 
		long orderId;
		String sorderId; //订单id
		Map<String,List<OrderItem>> gsom = som.getThirdSuccessOrderMap();
		Set<String> keySet = gsom.keySet();
	    for(String k : keySet) {
	        orderId = valueOpt.increment("order_id");
			sorderId = String.valueOf(orderId);//订单的唯一id
			soi.setId(orderId);
			soi.setSid(Integer.parseInt(k));
			soi.setFirstname(ci.getFirstname());
			soi.setLastname(ci.getLastname());
			soi.setAddress(ci.getAddress());
			soi.setTelephone(ci.getTelephone());
			soi.setPlist(gsom.get(k));
			soi.setStatus(OrderStatus.NOTPAYED.toString());
	        t = Instant.now().getEpochSecond();
		    soi.setCreated(t); //设置创建时间
		    //生成订单，插入到Redis
		    hashOpt.put("orders", sorderId, soi);
		
		    stringHashOpt.put("orders_view", sorderId, OrderStatus.NOTPAYED.toString());
	        //orders_hashtable与orders_zset协同管理订单
		    stringHashOpt.put("orders_hashtable",sorderId,String.valueOf(t));
		    zsetOpt.add("orders_zset", sorderId, t);
		    //orders_owner映射订单属于哪个库房，或哪个第三方店铺
		    //hashOpt.put("orders_owner", String.valueOf(orderId), sid);
	    }
	}
	
	/**
	 * 处理不在当前库房的自营小件商品的订单。
	 * TODO:具体业务中，有些缺货商品只从特定的库房调配，比如商品A，在9号库房
	 * 查不到的时候，值能从1号库房调配，这时候dispatchSelfSmallOrder
	 * 函数不适用，针对这一种情况要添加过滤逻辑，在分发订单的时候过滤出这种商品，
	 * 将该商品交由特定库房处理。
	 * @param shid 当前库房
	 * @param out  需要从别处调配的商品
	 * @param result 结果集，存储 缺货的商品
	 */
	public void dispatchSelfSmallOrder(int shid, List<OrderItem> out,SubmitOrderMiddle som) {
		int nshid = findNearestStoreHouse(shid); //离shid库房最近的库房，或能为shid库房供货的库房，由具体的物流信息决定
		List<OrderItem> sin = new ArrayList<>(); //存放在nshid仓库的小件商品
		List<OrderItem> sout = new ArrayList<>();//不在nshid仓库的小件商品
		
		divideSelfSmallOrder(nshid,out,sin,sout);
		
	    //商品全部在本仓库
	    if(sin.size() == out.size()) {
			checkAndUpdateInventory(nshid,-1,sin,som);
			return ;
		}else{ 
		    if(sin.size() == 0) {
		        //全部在外仓库
				dispatchSelfSmallOrder(nshid,sout,som);
				return ;
			}else {
				checkAndUpdateInventory(nshid,-1,sin,som);
				dispatchSelfSmallOrder(nshid,sout,som);
	            return;
			}	
		}		
	}
	
	/**
	 * 回滚订单
	 * @param soi 存储订单信息
	 */
	public void inventoryRollback(String ht, List<OrderItem> oil) {
		int ib;       //调试用，回滚前的值
		//int ia;
		int sg;       //判断商品种类
		String field; //商品在Redis中的域
		int num;      //商品的订购数量
	   	for(OrderItem oi : oil) { //对订单中的每件商品
			sg = oi.getIs();
			field =  String.valueOf(oi.getId());
			num = oi.getNum();
			//返回值
			ib = (int)hashOpt.get(ht, field);
	        if(sg==0) {
            	assert ib + num >= 0 && ib + num <=50:"大件数量不合法";
            }else if(sg==1){
            	assert ib + num >=0 && ib + num <= 30: "小件数量不合法";
            }else {
            	assert ib + num >=0 && ib + num <=15: "三方货物数量不合法";
            }
			hashOpt.increment(ht, field, num); //增加库存
	    }
	}
	
	/**
	 * 订单过期,回滚订单
	 * @param soi
	 */
	public void orderRollback(SubmitOrderItem soi) {
		String ht;
		String orderId = String.valueOf(soi.getId());
		int shid = soi.getShid();
		if(shid!=-1) {
			ht = String.valueOf(shid) + "invent";
			inventoryRollback(ht,soi.getPlist());
		}else {
			ht = "thirdinvent";
			inventoryRollback(ht,soi.getPlist());
		}
		hashOpt.delete("orders", orderId);
	}
	
	/**
	 * 存货不足，删除已经生成的订单，增加已经修改的商品的库存，
	 * 在Redis中每个库房有自己专属的哈希表key，比如存储库房 10里的产品
	 * 的哈希表key为"10invent",在表里，域为产品的字符串id,值为库存数量;
	 * 存储由第三方店铺销售的产品的哈希表key为"thirdinvent"
	 * @param som 存储已经添加的订单
	 */
	public void handleLackOfInventory(SubmitOrderMiddle som) {
		Set<String> keySet;
		List<OrderItem> oiList;
		String ht;
		if(som.getSelfSuccessOrderMap().size()!=0) {
			keySet = som.getSelfSuccessOrderMap().keySet();
			for(String key : keySet) {
				ht = key+"invent";
				oiList = som.getSelfSuccessOrderMap().get(key);
				inventoryRollback(ht,oiList);
			}
		}
		
		if(som.getThirdSuccessOrderMap().size()!=0) {
			keySet = som.getThirdSuccessOrderMap().keySet();
			for(String key : keySet) {
				ht = "thirdinvent";
				oiList = som.getThirdSuccessOrderMap().get(key);
				inventoryRollback(ht,oiList);
			}
		}		
	}
	
	/**
	 * 根据店铺id分拆订单,归集商品到所属的店铺
	 * @param tds 订单中属于第三方商铺的所有商品列表
	 * @param soo 值结果列表，店铺到属于自己店铺商品的list的映射
	 * Map<String,List<OrderItem>> soo 之前为Map<Integer,List<OrderItem>> soo,
	 * 涉及自动装箱和拆箱 
	 */
	public void divideGoodsByShopId(List<OrderItem>tds,Map<String,List<OrderItem>> soo) {
		int i;
		String sid;
		for(OrderItem oi : tds) {
			 i = oi.getSid();
			sid = String.valueOf(i);
	        if(	!soo.containsKey(sid)) { //如果键集中不包括该店铺
	        	soo.put(sid, new ArrayList<OrderItem>());
	        	soo.get(sid).add(oi);
	        }else {
	        	soo.get(sid).add(oi);
            }
		}
	}
	
	/**
	 * 处理订单配送。
	 * 当订单状态变为已付款后，需要将订单的信息写入MySQL，同时，订单也进入了后台的配送系统
	 * @param orderId 订单
	 */
	public void handlePayedOrder(String orderId) {
		//从Redis获取订单信息
		SubmitOrderItem soi = (SubmitOrderItem) hashOpt.get("orders", orderId);
		
		int shid = soi.getShid();
		String key = "order_in_storehouse_"+String.valueOf(shid);
		//还未出库的订单,加入订单所属的库房，由库房负责出库
		setOpt.add(key, soi);
		//第三方订单和自营订单orderId应该分开,设立两个键 
		//开始写入MySQL　
		writePayedOrderToMySQL(soi);
	}
	
	/**
	 * 修改数据数据库表，对于自营订单有库房的shid和店铺的sid,对于三方的订单只有店铺sid,没有shid 
	 * @param soi
	 */
	public void writePayedOrderToMySQL(SubmitOrderItem soi) {
		if(soi.getShid()!=-1) {
			orderService.selfInsert(soi); 
            for(OrderItem oi:soi.getPlist()) {
            	orderItemService.selfInsert(soi.getId(), oi.getId(), oi.getNum());
            	productService.selfUpdate(soi.getShid(), oi.getId(), oi.getNum());
            }
		}else{
			orderService.thirdInsert(soi);
			for(OrderItem oi:soi.getPlist()) {
				orderItemService.thirdInsert(soi.getId(), oi.getId(), oi.getNum());
            	productService.thirdUpdate(oi.getId(), oi.getNum());
			}
		}
	}
	
	//将过期订单加入数据库，不更新库存
	public void writeExpiredOrderToMySQL(SubmitOrderItem soi) {
		if(soi.getShid()!=-1) {
			orderService.selfInsert(soi); 
            for(OrderItem oi:soi.getPlist()) {
            	orderItemService.selfInsert(soi.getId(), oi.getId(), oi.getNum());
            }
		}else{
			orderService.thirdInsert(soi);
			for(OrderItem oi:soi.getPlist()) {
				orderItemService.thirdInsert(soi.getId(), oi.getId(), oi.getNum());
			}
		}
	}
	
	/**
	 * 在Redis服务中执行存货更新脚本。如果库存足够,则更新库存数量;如果存货不足,则返回缺货的产品id。
	 * 服务返回的是字符串,格式为"id1:id2:id3:",表示的意思是id1,id2,id3产品缺货。如果返回的
	 * 是"t",代表要订购的产品都不缺货,且已更新库存
	 * @param shid 库房id
	 * @param oo   要订购的产品
	 * @param som 
	 */
	public void checkAndUpdateInventory(int shid, int sid, List<OrderItem>oo,
			SubmitOrderMiddle som){
		String result;
		List<String> keys = new ArrayList<>(); 
		List<String> args = new ArrayList<>();
		
		if(shid!=-1) {  //自营商品
			keys.add(String.valueOf(shid)+"invent");
		}else {
			keys.add("thirdinvent");
		}
				
		for(OrderItem oi : oo) {
			keys.add(String.valueOf(oi.getId()));
			args.add(String.valueOf(oi.getNum()));
		}
		
		result = stringRedisTemplate.execute(stockCheckAndUpdate,keys, args.toArray());
		
		if(result.equals("t")) {  //所有商品均有库存
			if(shid!=-1) {  //自营商品
				som.getSelfSuccessOrderMap().put(String.valueOf(shid), oo);
			}else {
				som.getThirdSuccessOrderMap().put(String.valueOf(sid), oo);
			}
		}else {
			parseScriptExcuteResult(result,som);
		}
	}
	
	/**
	 * 解析Redis服务器执行脚本后返回的结果,将缺货的产品加入缺货列表
	 * @param result Redis服务器执行脚本后返回的结果
	 * @param som
	 */
	public void parseScriptExcuteResult(String result, SubmitOrderMiddle som) {
		 int len = result.length();
		 int d = ':';
		 int idx = 0;
		 int begin = 0; 
		 while(idx<len) {
		    idx = result.indexOf(d,idx);
		    som.getNotEnoughStockList().add(result.substring(begin,idx));
		    idx++;
		    begin = idx;
		 }
	}
}


