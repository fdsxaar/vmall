package com.ecommerce.vmall.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
class RedisInitConfig {

  @Bean
  public LettuceConnectionFactory redisConnectionFactory() {
    return new LettuceConnectionFactory(new RedisStandaloneConfiguration("127.0.0.1", 6379));
  }
 
  
  @Bean 
  public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory lcf) {
	  final RedisTemplate< String, Object > template =  new RedisTemplate< String, Object >();
	  template.setConnectionFactory(lcf);
	  GenericJackson2JsonRedisSerializer gjj2j = new GenericJackson2JsonRedisSerializer();
	  template.setValueSerializer(gjj2j);
      //使用StringRedisSerializer来序列化和反序列化redis的key值
      template.setKeySerializer(new StringRedisSerializer());

      // 设置hash key 和value序列化模式
      template.setHashKeySerializer(new StringRedisSerializer());
      template.setHashValueSerializer(gjj2j);
      template.afterPropertiesSet();
	  return template;
	  
  }

}