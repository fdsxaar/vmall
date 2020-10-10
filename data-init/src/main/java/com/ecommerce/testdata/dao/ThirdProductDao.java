package com.ecommerce.testdata.dao;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.ecommerce.testdata.pojo.ThirdProduct;

@Mapper
public interface ThirdProductDao {

	@Insert("INSERT INTO third_product(id,sid,inventory)"
			+ "VALUES(#{id},#{sid},#{ity})")
	int insert(int id,int sid,int ity);
	
	@Select("SELECT * from third_product")
	List<ThirdProduct> selectAll();
}
