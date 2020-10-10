package com.ecommerce.testdata.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShopDao {

	@Insert("INSERT INTO shop(id,name)"
			+ "VALUES(#{id},#{name})")
	int insert(int id, String name);
}
