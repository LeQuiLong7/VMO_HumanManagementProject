package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.request.login.LogoutRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.dto.response.LogoutResponse;
import com.lql.humanresourcedemo.service.login.LoginService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/sign-out")
    public LogoutResponse logout(@RequestBody @Valid LogoutRequest logoutRequest) {
        return loginService.logout(logoutRequest.token());
    }
}
