package com.ecommerce.testdata.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.testdata.dao.StorehouseDao;
import com.ecommerce.testdata.pojo.Storehouse;


@Service
public class StorehouseService {

	@Autowired
	StorehouseDao storehouseDao;
	
	public int insert(int shid, int nshid) {
		return storehouseDao.insert(shid, nshid);
	}
	
	public List<Storehouse> select(){
		return storehouseDao.select();
	}
}
