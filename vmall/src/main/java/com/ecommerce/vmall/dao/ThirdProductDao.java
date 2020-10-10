package com.ecommerce.vmall.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ThirdProductDao {

	@Insert("INSERT INTO third_product(id,sid,inventory)"
			+ "VALUES(#{id},#{sid},#{ity})")
	int insert(int id,int sid,int ity);
}
