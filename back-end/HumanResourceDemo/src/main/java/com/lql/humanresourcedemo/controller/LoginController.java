package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.login.ExchangeTokenRequest;
import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.request.login.LogoutRequest;
import com.lql.humanresourcedemo.dto.response.login.LoginResponse;
import com.lql.humanresourcedemo.dto.response.login.LogoutResponse;
import com.lql.humanresourcedemo.service.login.LoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
@Tag(name="1. Login controller")
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }
    @PostMapping("/exchange")
    public LoginResponse exchangeToken(@RequestBody @Valid ExchangeTokenRequest request) {
        return loginService.exchangeToken(request.sessionId());
    }
    @PostMapping("/sign-out")
    public LogoutResponse logout(@RequestBody @Valid LogoutRequest logoutRequest) {
        return loginService.logout(logoutRequest.token());
    }
}
