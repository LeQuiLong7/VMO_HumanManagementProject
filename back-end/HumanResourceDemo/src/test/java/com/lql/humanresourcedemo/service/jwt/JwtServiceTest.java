package com.lql.humanresourcedemo.service.jwt;

import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.enumeration.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;

import static com.lql.humanresourcedemo.constant.JWTConstants.ROLE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    private final String MOCK_SECRET_KEY = "rcvDhGcLJNUiog9CvUKc+sbs4cojMAnMkALj5VDiXCE=";
    private JwtBuilder jwtBuilder;
    private JwtParser jwtParser;

    private SecretKey getSecretKey() {
        byte[] bytes = Decoders.BASE64.decode(MOCK_SECRET_KEY);
        return Keys.hmacShaKeyFor(bytes);
    }

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtBuilder = Jwts.builder().signWith(getSecretKey(), SignatureAlgorithm.HS256);
        jwtParser = Jwts.parserBuilder().setSigningKey(getSecretKey()).build();
        jwtService = new JWTServiceImpl(jwtBuilder, jwtParser, 60000, "MINUTES");
    }


    @Test
     void generateTokenTest() {
        OnlyIdPasswordAndRole employee = new OnlyIdPasswordAndRole(1L, "", Role.ADMIN);

        String token = jwtService.generateToken(employee);

        Long id = Long.valueOf(jwtService.extractClaim(token, Claims::getSubject));
        Role role = Role.valueOf(jwtService.extractClaim(token, claim -> claim.get(ROLE).toString()));


        assertAll(
            () -> assertEquals(employee.id(), id),
            () -> assertEquals(employee.role(), role)
        );
    }

    @Test
     void extractAllClaimsTest() {
        OnlyIdPasswordAndRole employee = new OnlyIdPasswordAndRole(1L, "", Role.ADMIN);

        String token = jwtService.generateToken(employee);

        Claims claims = jwtService.extractAllClaims(token);


        assertAll(
                () -> assertThat(claims).hasSize(4),
                () -> assertThat(claims).containsKeys("sub", "role", "exp", "iat"),
                () -> assertThat(claims.getSubject()).isEqualTo("1"),
                () -> assertThat(claims.get("role")).isEqualTo("ADMIN")
        );
    }

    @Test
     void notValidTokenTest() {

        String token = "abc";

        assertThrows(JwtException.class,
                () -> jwtService.extractAllClaims(token));
    }


    @Test
     void expiredTokenTest() {

        jwtService = new JWTServiceImpl(jwtBuilder, jwtParser, -60000, "MINUTES");

        OnlyIdPasswordAndRole employee = new OnlyIdPasswordAndRole(1L, "", Role.ADMIN);

        String token = jwtService.generateToken(employee);

        assertTrue(
                () -> jwtService.isTokenExpired(token));
    }

    @Test
     void expiredNotValidTokenTest() {

        String token = "abc";
        assertThrows(JwtException.class,
                () -> jwtService.isTokenExpired(token));
    }

}