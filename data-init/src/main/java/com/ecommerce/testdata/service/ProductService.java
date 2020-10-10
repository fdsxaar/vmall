package com.ecommerce.testdata.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.testdata.dao.ProductDao;
import com.ecommerce.testdata.pojo.SelfIty;


@Service
public class ProductService {

	@Autowired
	ProductDao productDao;

	public List<SelfIty> findAll(int sid){
		return productDao.findAll(sid);
	}
	
	public int getInventoryByStoreHouseIDAndProductID(int storeHouseId, int productId) {
		return productDao.getInventoryByStoreHouseIDAndProductID(storeHouseId, productId);
	}
	
	//更新自营产品库存
	public int selfUpdate(int storeHouseId, int productID, int num) {
		return productDao.selfUpdate(storeHouseId, productID, num);
	}
	
	//更新第三方产品库存 
	public int thirdUpdate(int id, int num) {
		return productDao.thirdUpdate(id,num);
	}
	
	public int updateInventoryForOrder(int shid, int id, int num) {
		return productDao.updateInventoryForOrder(shid,id,num);
	}
	
	
	public int selfInsert(int shid, int pid, int ity) {
		return productDao.selfInsert( shid,  pid,  ity);

	}

	
}
