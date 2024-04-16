package com.lql.humanresourcedemo.service.jwt;


import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.enumeration.Role;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.lql.humanresourcedemo.constant.JWTConstants.*;

@Service
@RequiredArgsConstructor
public class JWTServiceImpl implements JwtService {
    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    @Override
    public   <T> T extractClaim(Claims claims, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(claims);
    }
    @Override
    public Claims extractAllClaims(String token) {
        return  jwtParser
                .parseClaimsJws(token)
                .getBody();
    }

    @Override
    public String generateToken( OnlyIdPasswordAndRole e) {

        return  jwtBuilder
                .setClaims(buildClaims(e))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + (1000 * 60
                        * 1000)))
                .compact();
    }

    private  Map<String, String>  buildClaims(OnlyIdPasswordAndRole e) {
        Map<String, String> claims = new HashMap<>();

        claims.put("sub", String.valueOf(e.id()));
        claims.put("role", e.role().toString());
        return claims;
    }

    @Override
    public boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException e) {
            throw e;
        }
    }
}
