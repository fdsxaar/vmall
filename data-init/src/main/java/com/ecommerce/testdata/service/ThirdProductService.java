package com.ecommerce.testdata.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.testdata.dao.ThirdProductDao;
import com.ecommerce.testdata.pojo.ThirdProduct;


@Service
public class ThirdProductService {

	@Autowired
	ThirdProductDao thirdProductDao;
	
	public int insert(int pid, int sid,int ity) {
		return thirdProductDao.insert(pid,sid,ity);
	}
	
	public List<ThirdProduct> selectAll(){
		return thirdProductDao.selectAll();
	}

}
