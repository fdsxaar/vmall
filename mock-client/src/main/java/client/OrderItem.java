package client;

import lombok.Data;

@Data
public class OrderItem {
  
	private int id;    //产品id
	private int is;    //0表示自营大件,1表示自营小件,2表示第三方货
	private int sid;   //店铺id
	private int num;   //购买的数
}
