package com.ecommerce.vmall.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.vmall.dao.ThirdProductDao;

@Service
public class ThirdProductService {

	@Autowired
	ThirdProductDao thirdProductDao;
	
	public int insert(int pid, int sid,int ity) {
		return thirdProductDao.insert(pid,sid,ity);
	}
	
}
