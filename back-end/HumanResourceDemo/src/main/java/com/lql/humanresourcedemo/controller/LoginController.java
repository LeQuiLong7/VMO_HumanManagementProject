package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.employee.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.service.login.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }
}
