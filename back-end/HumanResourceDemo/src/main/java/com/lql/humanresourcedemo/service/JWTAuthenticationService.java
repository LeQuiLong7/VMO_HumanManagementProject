package com.lql.humanresourcedemo.service;


import com.lql.humanresourcedemo.constant.JWTConstants;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.authentication.MyAuthentication;
import com.lql.humanresourcedemo.model.employee.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.lql.humanresourcedemo.constant.JWTConstants.*;

@Service
@RequiredArgsConstructor
public class JWTAuthenticationService {
    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;

    public Authentication convertToAuthentication(String token) {
        Claims claims = extractAllClaims(token);
        return new MyAuthentication(
                Long.parseLong(claims.getSubject()),
                Role.valueOf(claims.get(ROLE).toString())
        );
    }

    public Long extractEmployeeId(String token) {
        return  Long.parseLong(extractClaims(token, Claims::getSubject));
    }
    public Role extractRole(String token) {
        return Role.valueOf(extractClaims(token, claims -> claims.get(ROLE).toString()));
    }

    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }
    public Claims extractAllClaims(String token) {
        return  jwtParser
                .parseClaimsJws(token)
                .getBody();
    }


    public String generateToken( Employee e) {
        return  jwtBuilder
                .setSubject(String.valueOf(e.getId()))
                .setClaims(Map.of(ROLE, e.getRole()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (10 * 60
                        * 1000)))
                .compact();
    }

    public boolean isTokenExpired(String token) {
        return extractClaims(token, Claims::getExpiration).before(new Date(System.currentTimeMillis()));
    }
}












//    public boolean isTokenNotExpired(String token) {
//        return extractClaims(token, Claims::getExpiration).after(new Date(System.currentTimeMillis()));
//    }
//    public String generateConfirmToken(String username) {
//        return  jwtBuilder
//                .setSubject(username)
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + (15 * 60
//                        * 1000)))
//                .compact();
//    }

//    public boolean isConfirmationTokenValid(String token) {
//        return isTokenNotExpired(token);
//    }


//    public boolean isTokenValid(String token){
//        if (!isTokenNotExpired(token)) return false;
//        Token tokenObject = tokenRepository.findById(extractUserId(token)).orElseThrow(MyUsernameNotFoundException::new);
//        return tokenObject.isValid();
//
//    }
//    public boolean isTokenNotExpired(String token) {
//        return extractClaims(token, Claims::getExpiration).after(new Date(System.currentTimeMillis()));
//    }
