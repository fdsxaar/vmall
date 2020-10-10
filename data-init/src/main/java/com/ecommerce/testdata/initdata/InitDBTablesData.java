package com.ecommerce.testdata.initdata;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ecommerce.testdata.service.ProductService;
import com.ecommerce.testdata.service.ShopService;
import com.ecommerce.testdata.service.StorehouseService;
import com.ecommerce.testdata.service.ThirdProductService;

@Component
public class InitDBTablesData {
	@Autowired
	ProductService productService;
	@Autowired
	StorehouseService storehouseService;
	@Autowired
	ThirdProductService thirdProductService;
	@Autowired
	ShopService shopService;
	
	Random r = new Random(10000);

	//自营库房插入大件产品
	public void insertHeavyData() {
        int i,j;
        int k = 50;
		for(i=1; i<=34; i++) { //共34个库房
			for(j=1; j<=200; j++ ) { //每个库房200中商品
		        productService.selfInsert(i, j, k);
			}
		}
	}
	
	//自营库房插入小件商品,有的产品在某些库房没有存货 
	public void insertSmallData() {
		int i,j;
		int k = 30;
		for(i=1;i<=10;i++){
			for(j=201;j<=277;j++) {
				productService.selfInsert(i, j, k);
			}
		}
	}
	
	//插入库房信息
	public void insertStorehouse() {
		int i=34;
		int j=33;
		for(;i>1&&j>=1;i--,j--) {
			storehouseService.insert(i, j);
		}
		
		storehouseService.insert(1, -1);
	}
	
	//插入第三方的店铺和商品
	public void insertThirdProduct() {
		int i,j;
		int k=1;
		int n=15;
		for(i=1;i<10;i++) {
			shopService.insert(i,"shop"+String.valueOf(i));
			for(j=1;j<20;j++) {
				thirdProductService.insert(k, i,n);
				k++;
			}
		}
	}
}
