package com.lql.humanresourcedemo.service;


import com.lql.humanresourcedemo.dto.model.EmployeeDTO;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.security.MyAuthentication;
import com.lql.humanresourcedemo.model.employee.Employee;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.lql.humanresourcedemo.constant.JWTConstants.*;

@Service
@RequiredArgsConstructor
public class JWTAuthenticationService {
    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;

    public Long extractEmployeeId(String token) {
        return  Long.parseLong(extractClaim(token, Claims::getSubject));
    }
    public Long extractEmployeeId(Claims claims) {
        return  Long.parseLong(extractClaim(claims, Claims::getSubject));
    }
    public Role extractRole(String token) {
        return Role.valueOf(extractClaim(token, claims -> claims.get(ROLE).toString()));
    }
    public Role extractRole(Claims claims) {
        return Role.valueOf(extractClaim(claims, claim -> claims.get(ROLE).toString()));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    public   <T> T extractClaim(Claims claims, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(claims);
    }
    public Claims extractAllClaims(String token) {
        return  jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateToken( EmployeeDTO e) {

        return  jwtBuilder
                .setClaims(buildClaims(e))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (10 * 60
                        * 1000)))
                .compact();
    }

    private  Map<String, String>  buildClaims(EmployeeDTO e) {
        Map<String, String> claims = new HashMap<>();

        claims.put("sub", String.valueOf(e.id()));
        claims.put("role", e.role().toString());
        return claims;
    }

    public boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date(System.currentTimeMillis()));
    }
}





//    public Authentication convertToAuthentication(String token, String ipAddress) {
//        Claims claims = extractAllClaims(token);
//        return new MyAuthentication(
//                extractEmployeeId(claims),
//                extractRole(claims),
//                ipAddress
//        );
//    }




//
//    public String generateToken( Employee e) {
//        return  jwtBuilder
//                .setSubject(String.valueOf(e.getId()))
//                .setClaims(Map.of(ROLE, e.getRole()))
//                .setIssuedAt(new Date(System.currentTimeMillis()))
//                .setExpiration(new Date(System.currentTimeMillis() + (10 * 60
//                        * 1000)))
//                .compact();
//    }



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
