package com.ecommerce.vmall.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;


@Mapper
public interface ProductDao {
   //storeHouseId和productId查询表inventory,获得库存 
	@Select("SELECT inventory from product where storehouse_id=#{storeHouseId} "
			+ "and product_id=#{productId}")
	int getInventoryByStoreHouseIDAndProductID(int storeHouseId, int productId);
	
	//更新自营库存
	@Update("UPDATE self_product "
			+ "SET inventory=inventory-#{num} "
			+ "where storehouse_id=#{storeHouseId} and product_id=#{productId}")
	int selfUpdate(int storeHouseId, int productId, int num);
	
	//更新第三方商品库存 
	@Update("UPDATE third_product "
			+ "SET inventory=inventory-#{num} "
			+ "where id=#{productId}")
	int thirdUpdate(int productId, int num);
	
	//订购货物时候更新库存
	@Update("UPDATE product "
			+ "SET inventory=IF(inventory>=#{num},inventory-#{num},inventory) "
			+ "WHERE storehouse_id=#{shid} and product_id=#{id}")
	int updateInventoryForOrder(int shid, int id, int num);
	
	
	@Insert("INSERT INTO self_product(storehouse_id,product_id,inventory)"
			+ "VALUES(#{shid},#{pid},#{ity})")
	int selfInsert(int shid, int pid, int ity);
}
