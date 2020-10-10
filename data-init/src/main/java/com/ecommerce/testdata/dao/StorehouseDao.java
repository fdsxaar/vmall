package com.ecommerce.testdata.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.ecommerce.testdata.pojo.Storehouse;

@Mapper
public interface StorehouseDao {

	@Insert("INSERT INTO storehouse(shid,nshid)"
			+ "VALUES(#{shid},#{nshid})")
	int insert(int shid, int nshid);
	
	@Select("SELECT shid,nshid FROM storehouse")
	List<Storehouse> select();
}
