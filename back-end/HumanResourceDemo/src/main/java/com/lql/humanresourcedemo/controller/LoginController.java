package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.model.EmployeeDTO;
import com.lql.humanresourcedemo.dto.request.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.service.EmployeeService;
import com.lql.humanresourcedemo.service.JWTAuthenticationService;
import com.lql.humanresourcedemo.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }


}
