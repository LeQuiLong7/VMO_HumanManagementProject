package com.lql.humanresourcedemo.service;

import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.service.jwt.JWTServiceImpl;
import com.lql.humanresourcedemo.service.jwt.JwtService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static com.lql.humanresourcedemo.constant.JWTConstants.ROLE;
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

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtBuilder = Jwts.builder().signWith(getSecretKey(), SignatureAlgorithm.HS256);
        jwtParser = Jwts.parserBuilder().setSigningKey(getSecretKey()).build();
        jwtService = new JWTServiceImpl(jwtBuilder, jwtParser);
    }

    @Test
    public void generateTokenTest() {
        OnlyIdPasswordAndRole employee = new OnlyIdPasswordAndRole(1L, "", Role.ADMIN);

        String token = jwtService.generateToken(employee);
        System.out.println(token);

        Long id = Long.valueOf(jwtService.extractClaim(token, Claims::getSubject));
        Role role = Role.valueOf(jwtService.extractClaim(token, claim -> claim.get(ROLE).toString()));


        assertThrows(JwtException.class, () -> jwtService.isTokenExpired(token+"a"));

        assertAll(() -> {
            assertEquals(employee.id(), id);
            assertEquals(employee.role(), role);
        });

    }

}