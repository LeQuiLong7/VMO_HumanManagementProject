package com.lql.humanresourcedemo.config;


import com.lql.humanresourcedemo.dto.response.LoginResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedisTemplate<Long, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<Long, String> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        template.setKeySerializer(RedisSerializer.java());
        template.setValueSerializer(RedisSerializer.string());
        return template;
    }
    @Bean
    public RedisTemplate<String, LoginResponse> redisLoginTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, LoginResponse> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(RedisSerializer.json());
        return template;
    }
}
