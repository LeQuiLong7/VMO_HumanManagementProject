package com.lql.humanresourcedemo.filter;

import com.lql.humanresourcedemo.model.authentication.MyAuthentication;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.service.EmployeeService;
import com.lql.humanresourcedemo.service.JWTAuthenticationService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.lql.humanresourcedemo.constant.SecurityConstants.*;
import static org.springframework.http.HttpHeaders.*;


@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTAuthenticationService jwtAuthenticationService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        if(requestURI.equals(LOGIN_URI)) {

                filterChain.doFilter(request, response);
        } else {

            final String bearerToken = request.getHeader(AUTHORIZATION);

            if (bearerToken == null || !bearerToken.startsWith("Bearer ")) {
                response.setStatus(403);
                response.getWriter().print("No bearer token");
                return;
            }
            String token = bearerToken.substring(7);
            try {
                if(jwtAuthenticationService.isTokenExpired(token)) {
                    response.setStatus(403);
                    response.getWriter().print("token expired");
                    return;
                }
            } catch (JwtException e) {
                filterChain.doFilter(request, response);
                response.setStatus(403);
                response.getWriter().print("token is not valid");
                return;
            }

            Authentication authentication = jwtAuthenticationService.convertToAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        }
    }
}
