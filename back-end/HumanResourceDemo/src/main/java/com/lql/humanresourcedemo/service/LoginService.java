package com.lql.humanresourcedemo.service;

import com.lql.humanresourcedemo.dto.model.EmployeeDTO;
import com.lql.humanresourcedemo.dto.request.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.exception.model.EmailNotFoundException;
import com.lql.humanresourcedemo.exception.model.WrongPasswordException;
import com.lql.humanresourcedemo.model.employee.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class LoginService {
    private final EmployeeService employeeService;
    private final JWTAuthenticationService jwtService;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest loginRequest) {

        EmployeeDTO employee = employeeService.findByEmail(loginRequest.email(), EmployeeDTO.class)
                .orElseThrow(() -> new EmailNotFoundException(loginRequest.email()));

        if(!passwordEncoder.matches(loginRequest.password(), employee.password())) {
            throw new WrongPasswordException(loginRequest.email(), loginRequest.password());
        }

        return new LoginResponse(jwtService.generateToken(employee));

    };
}
