package com.lql.humanresourcedemo.config;


import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.crypto.SecretKey;

@Configuration
public class BeanConfig {

    @Value("${spring.jwt.secret-key}")
    private String secretKey;


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private SecretKey getSecretKey() {
        byte[] bytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    @Bean
    public JwtBuilder jwtBuilder() {
        return Jwts.builder().signWith(getSecretKey(), SignatureAlgorithm.HS256);

    }
    @Bean
    public JwtParser jwtParser() {
        return Jwts.parserBuilder().setSigningKey(getSecretKey()).build();

    }
}
