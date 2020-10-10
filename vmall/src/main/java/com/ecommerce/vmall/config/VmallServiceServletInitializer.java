package com.ecommerce.vmall.config;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.ecommerce.vmall.VmallApplication;

//配置类，用于生成WAR文件
public class VmallServiceServletInitializer 
            extends SpringBootServletInitializer{
      
	@Override
	protected SpringApplicationBuilder configure(
	                         SpringApplicationBuilder builder) {
	    return builder.sources(VmallApplication.class);
	  }
}
