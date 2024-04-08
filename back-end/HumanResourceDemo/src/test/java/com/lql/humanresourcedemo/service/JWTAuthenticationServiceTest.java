package com.lql.humanresourcedemo.service;

import com.lql.humanresourcedemo.dto.model.EmployeeDTO;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import io.jsonwebtoken.*;
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
    public void generateTokenTest() {
        EmployeeDTO employee = new EmployeeDTO(1L, "", Role.ADMIN);

        String token = jwtAuthenticationService.generateToken(employee);
        System.out.println(token);

        Long id = jwtAuthenticationService.extractEmployeeId(token);
        Role role = jwtAuthenticationService.extractRole(token);


        assertThrows(JwtException.class, () -> jwtAuthenticationService.isTokenExpired(token+"a"));

        assertAll(() -> {
            assertEquals(employee.id(), id);
            assertEquals(employee.role(), role);
        });

    }

}