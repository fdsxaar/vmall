package com.ecommerce.testdata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.testdata.dao.ShopDao;


@Service
public class ShopService {
    
	@Autowired 
	ShopDao shopDao;
	
	public int insert(int id, String name) {
		return shopDao.insert(id,name);
	}

	

}
