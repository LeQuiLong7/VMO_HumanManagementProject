package com.lql.humanresourcedemo.service.jwt;


import com.lql.humanresourcedemo.constant.JWTConstants;
import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.utility.MappingUtility;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static com.lql.humanresourcedemo.constant.JWTConstants.*;

@Service
//@RequiredArgsConstructor
public class JWTServiceImpl implements JwtService {
    private final JwtBuilder jwtBuilder;
    private final JwtParser jwtParser;
    private final long EXPIRED_DURATION;
    private final String EXPIRED_TIME_UNIT;

    public JWTServiceImpl(JwtBuilder jwtBuilder,
                          JwtParser jwtParser,
                          @Value("${jwt.expiration.duration}") long EXPIRED_DURATION,
                          @Value("${jwt.expiration.time-unit}") String EXPIRED_TIME_UNIT) {
        this.jwtBuilder = jwtBuilder;
        this.jwtParser = jwtParser;
        this.EXPIRED_DURATION = EXPIRED_DURATION;
        this.EXPIRED_TIME_UNIT = EXPIRED_TIME_UNIT;
    }

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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plus(EXPIRED_DURATION, ChronoUnit.valueOf(EXPIRED_TIME_UNIT));

        return  jwtBuilder
                .setClaims(buildClaims(e))
                .setExpiration(MappingUtility.toDate(now))
                .setExpiration(MappingUtility.toDate(expirationTime))
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
