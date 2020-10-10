package client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class GenOrder {
	Random r ;
	
    public void initRandom() {
    	r = new Random(System.nanoTime());
    }
    
	public CustomerInfo genCustomerInfo() {
		CustomerInfo ci = new CustomerInfo();

		ci.setFirstname("Bruce");
		ci.setLastname("Li");
		ci.setAddress("USA NY");
		ci.setTelephone("01-75-690");
		
		return ci;
	}
	
	public Order genOrder() {
		Order order = new Order();
		initRandom();
		int n,id;
		n = Math.abs(r.nextInt());
		int shid = n % 35;
		order.setShid((shid==0)?(shid+1):shid);
		order.setCi(genCustomerInfo());
		List<OrderItem> oil = new ArrayList<>();
		
        Map<String,OrderItem> tmap = new HashMap<>();
		
		for(int i=3;i>0;i--) {
			OrderItem oi = new OrderItem();
			n = Math.abs(r.nextInt());
			id = n % 201;
		    id=(id==0)?(id+1):id;
			
			oi.setId(id);
			oi.setIs(0);
		    int num = n % 51;
			oi.setNum((num==0)?(num+1):num);
			tmap.put(String.valueOf(id), oi);
		
		}
	   
	   	tmap.forEach((k,v)->oil.add(v));
        tmap.clear();
		
		//添加订单中的小件,201到277编号的产品为小件，只有1-10号库房有
		for(int i=3;i>0;i--) {
			OrderItem oi = new OrderItem();

			n = Math.abs(r.nextInt());
            id = (n % 77)+201;
			
			 oi.setId(id);
            oi.setIs(1);
            int num = n % 31;
            oi.setNum((num==0)?(num+1):num);
            tmap.put(String.valueOf(id), oi);
		}
	
	   	tmap.forEach((k,v)->oil.add(v));
        tmap.clear();
		//添加订单中的第三方店铺出售的商品
		for(int i=3;i>0;i--) {
			OrderItem oi = new OrderItem();
			
			n = Math.abs(r.nextInt());
		    id = n % 172;
		  
			oi.setId((id==0)?(id+1):id);
            oi.setIs(2);
            
            int sid = n % 10;
			oi.setSid((sid==0)?(sid+1):sid);

            int num = n % 16;
            oi.setNum((num==0)?(num+1):num);
            tmap.put(String.valueOf(id), oi);
		}
		
	   	tmap.forEach((k,v)->oil.add(v));
	   	tmap.clear();
		order.setList(oil);
		
		return order;
	}
	
	public String genJsonOrder() {
        Order o = genOrder();
		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String jsonOrder = null;
		try {
			jsonOrder = ow.writeValueAsString(o);
			//new JSONObject(ow.writeValueAsString(msg)) 
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(jsonOrder);
		return jsonOrder;
	}
}
