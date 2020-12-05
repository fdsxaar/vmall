package com.ecommerce.vmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.scripting.ScriptSource;
import org.springframework.scripting.support.ResourceScriptSource;

@Configuration
public class RedisScriptConfig {

	@Bean
	public RedisScript<String> stockCheckAndUpdate() {
		DefaultRedisScript<String> redisScript = new DefaultRedisScript<>();
		ScriptSource scriptSource = new ResourceScriptSource(new ClassPathResource("/scripts/stockCheckAndUpdate.lua"));
        redisScript.setScriptSource(scriptSource); 
		redisScript.setResultType(String.class); //去掉这一行会报错
		
		return redisScript;
	}
}
