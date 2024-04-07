package com.lql.humanresourcedemo.service;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JWTAuthenticationServiceTest {
    private String mockSecretKey = "rcvDhGcLJNUiog9CvUKc+sbs4cojMAnMkALj5VDiXCE=";
    private JwtBuilder jwtBuilder;
    private JwtParser jwtParser;

    private SecretKey getSecretKey() {
        byte[] bytes = Decoders.BASE64.decode(mockSecretKey);
        return Keys.hmacShaKeyFor(bytes);
    }

    private JWTAuthenticationService jwtAuthenticationService;

    @BeforeEach
    void setUp() {
        jwtBuilder = Jwts.builder().signWith(getSecretKey(), SignatureAlgorithm.HS256);
        jwtParser = Jwts.parserBuilder().setSigningKey(getSecretKey()).build();
        jwtAuthenticationService = new JWTAuthenticationService(jwtBuilder, jwtParser);
    }


    @Test
    public void extractTest() {
        Employee employee = Employee.builder()
                .id(12L)
                .role(Role.ADMIN)
                .build();

        String s = jwtAuthenticationService.generateToken(employee);


        assertDoesNotThrow(() -> {
            Role role = jwtAuthenticationService.extractRole(s);

            assertThat(role).isNotNull();
            System.out.println(role);
        });

    }
}