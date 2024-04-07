package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.service.EmployeeService;
import com.lql.humanresourcedemo.service.JWTAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private final JWTAuthenticationService jwtAuthenticationService;
    private final EmployeeService employeeService;


    @GetMapping
    public String getToken() {

        return jwtAuthenticationService.generateToken(employeeService.findById(1L).get());
    }
}
