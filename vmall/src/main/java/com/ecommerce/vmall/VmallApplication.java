package com.ecommerce.vmall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan(value = {"com.ecommerce.vmall.dao"})
@EnableScheduling
public class VmallApplication {

	public static void main(String[] args) {
		SpringApplication.run(VmallApplication.class, args);
	}

}
