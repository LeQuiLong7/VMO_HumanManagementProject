package com.lql.humanresourcedemo.filter;

import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.service.login.LoginService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class Oauth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final LoginService loginService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String email = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttribute("email");
        try {
            LoginResponse loginResponse = loginService.loginWithOauth2(email);
            response.sendRedirect("http://localhost:5173/login?token=%s&type=%s&role=%s".formatted(loginResponse.token(), loginResponse.type(), loginResponse.role()));
        } catch (LoginException ex) {
            response.sendRedirect("http://localhost:5173/login?error=%s".formatted(ex.getMessage()));
        }
    }
}
