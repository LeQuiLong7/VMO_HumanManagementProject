package com.lql.humanresourcedemo.filter;

import com.lql.humanresourcedemo.dto.response.login.LoginResponse;
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
                                              @Value("${jwt.expiration.duration}") final long EXPIRED_DURATION,
                                              @Value("${jwt.expiration.time-unit}") final String EXPIRED_TIME_UNIT) {
        this.loginService = loginService;
        this.redisLoginTemplate = redisLoginTemplate;
        this.EXPIRED_DURATION = EXPIRED_DURATION;
        this.EXPIRED_TIME_UNIT = EXPIRED_TIME_UNIT;
    }

    @Override
    // redirect to /login on front end with a session id as parameter
    // front end side will use that session id to exchange for a jwt token
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // get the email address from the OAuth2AuthenticationToken when user authenticate successfully with google
        String email = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");
        try {

            LoginResponse loginResponse = loginService.loginWithOauth2(email);
            // create an uuid as the session id
            String sessionId = UUID.randomUUID().toString();
            // store the login response in redis with the key is the uuid that is just created
            redisLoginTemplate.opsForValue().set(sessionId, loginResponse,  Duration.of(EXPIRED_DURATION, ChronoUnit.valueOf(EXPIRED_TIME_UNIT)));

            // redirect to front end with the uuid session id
            response.sendRedirect("http://localhost:5173/login?sessionId=%s".formatted(sessionId));

        } catch (LoginException ex) {
            // the email does not exist in the system
            response.sendRedirect("http://localhost:5173/login?error=%s".formatted(ex.getMessage()));
        }
    }
}
