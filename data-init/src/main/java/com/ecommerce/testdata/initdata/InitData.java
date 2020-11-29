package com.ecommerce.testdata.initdata;

import javax.sql.DataSource;
import javax.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InitData implements CommandLineRunner {

	@Autowired
	DataSource dataSource;
	@Autowired
	StringRedisTemplate stringRedisTemplate;
	 
	@Resource(name="stringRedisTemplate")
	private ValueOperations<String, String> valueOpt;
	
	@Autowired
	InitDBTablesData initDBTablesData;
	@Autowired
	InitRedisData initRedisData;

	public void run(String... args) {
		ScriptRunner runner;
		try {
			Thread.sleep(10);
			valueOpt.set("k8s","redis-ok");
			System.out.println("获取mysql连接");
			runner = new ScriptRunner(dataSource.getConnection());
			System.out.println("成功获取mysql连接");
			runner.setAutoCommit(true);
			runner.setStopOnError(true);
			runner.setLogWriter(null);
			//初始化数据库的表
			System.out.println("开始初始化数据");
			runner.runScript(Resources.getResourceAsReader("scripts/drop_table.sql"));
			runner.runScript(Resources.getResourceAsReader("scripts/self_order.sql"));
			runner.runScript(Resources.getResourceAsReader("scripts/self_order_item.sql"));
			runner.runScript(Resources.getResourceAsReader("scripts/self_product.sql"));
			runner.runScript(Resources.getResourceAsReader("scripts/shop.sql"));
			runner.runScript(Resources.getResourceAsReader("scripts/storehouse.sql"));
			runner.runScript(Resources.getResourceAsReader("scripts/third_order.sql"));
			runner.runScript(Resources.getResourceAsReader("scripts/third_order_item.sql"));
			runner.runScript(Resources.getResourceAsReader("scripts/third_product.sql"));

			//初始化数据库表的数据
			initDBTablesData.insertHeavyData(); 
			initDBTablesData.insertSmallData();
			initDBTablesData.insertStorehouse();
			initDBTablesData.insertThirdProduct();
			
			//初始化Redis的业务数据
			initRedisData.deleteOldKeys();
			initRedisData.writeInventory();
			initRedisData.writeStorehouseInfo();	
			initRedisData.writeThirdGood();
			initRedisData.initOrderId();
			
		}catch (Exception e) {
			System.out.println("初始化数据错误！");
		}
		System.out.println("完成数据初始化！");
    }
}
