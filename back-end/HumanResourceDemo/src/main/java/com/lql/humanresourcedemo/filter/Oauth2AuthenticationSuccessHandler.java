package com.lql.humanresourcedemo.filter;

import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.service.login.LoginService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final LoginService loginService;
    private final RedisTemplate<String, LoginResponse> redisLoginTemplate;
    private final long EXPIRED_DURATION;
    private final String EXPIRED_TIME_UNIT;

    public Oauth2AuthenticationSuccessHandler(LoginService loginService,
                                              RedisTemplate<String, LoginResponse> redisLoginTemplate,
                                              @Value("${jwt.expiration.duration}") long EXPIRED_DURATION,
                                              @Value("${jwt.expiration.time-unit}") String EXPIRED_TIME_UNIT) {
        this.loginService = loginService;
        this.redisLoginTemplate = redisLoginTemplate;
        this.EXPIRED_DURATION = EXPIRED_DURATION;
        this.EXPIRED_TIME_UNIT = EXPIRED_TIME_UNIT;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");
        try {
            LoginResponse loginResponse = loginService.loginWithOauth2(email);
            String sessionId = UUID.randomUUID().toString();
            redisLoginTemplate.opsForValue().set(sessionId, loginResponse,  Duration.of(EXPIRED_DURATION, ChronoUnit.valueOf(EXPIRED_TIME_UNIT)));
            response.sendRedirect("http://localhost:5173/login?sessionId=%s".formatted(sessionId));

        } catch (LoginException ex) {
            response.sendRedirect("http://localhost:5173/login?error=%s".formatted(ex.getMessage()));
        }
    }
}
