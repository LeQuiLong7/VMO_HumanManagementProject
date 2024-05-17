package com.lql.humanresourcedemo.service.jwt;

import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import io.jsonwebtoken.Claims;

import java.util.function.Function;

public interface JwtService {
    String generateToken( OnlyIdPasswordAndRole e);
    Claims extractAllClaims(String token);
    <T> T extractClaim(Claims claims, Function<Claims, T> claimsResolver);
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
    boolean isTokenExpired(String token);
}
