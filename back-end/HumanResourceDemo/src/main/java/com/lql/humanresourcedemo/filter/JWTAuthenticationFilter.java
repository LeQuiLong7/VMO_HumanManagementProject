package com.lql.humanresourcedemo.filter;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.security.MyAuthentication;
import com.lql.humanresourcedemo.service.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.lql.humanresourcedemo.constant.JWTConstants.ROLE;
import static com.lql.humanresourcedemo.constant.SecurityConstants.*;
import static org.springframework.http.HttpHeaders.*;


@Component
@RequiredArgsConstructor
@Slf4j
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();


        if (!PUBLIC_URI.contains(requestURI)) {

            final String bearerToken = request.getHeader(AUTHORIZATION);

            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                log.warn("Access denied: Someone trying to access {} Method: {} without a bearer token - IP address: {} ",  request.getRequestURI(), request.getMethod(), request.getRemoteAddr());
                response.setStatus(403);
                response.getWriter().print("No bearer token");
                return;
            }
            String token = bearerToken.substring(7);
            try {
                if (jwtService.isTokenExpired(token)) {
                    log.warn("Access denied: Someone trying to access {} Method: {} with a expired token - IP address: {} ", request.getRequestURI(), request.getMethod(), request.getRemoteAddr());
                    response.setStatus(403);
                    response.getWriter().print("token expired");
                    return;
                }
            } catch (JwtException e) {
                log.warn("Access denied: Someone trying to access {} Method: {} with a invalid token : {} - IP address: {} ", request.getRequestURI(), request.getMethod(),token, request.getRemoteAddr());
                response.setStatus(403);
                response.getWriter().print("token is not valid");
                return;
            }
            MyAuthentication authentication = new MyAuthentication();

            Claims claims = jwtService.extractAllClaims(token);

            authentication.setEmployeeId(jwtService.extractClaim(claims, claim ->  Long.parseLong(claim.getSubject())));
            authentication.setRole(Role.valueOf(jwtService.extractClaim(claims, claim -> claim.get(ROLE).toString())));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
