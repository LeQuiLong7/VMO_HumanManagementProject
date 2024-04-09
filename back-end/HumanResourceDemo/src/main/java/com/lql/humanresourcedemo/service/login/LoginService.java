package com.lql.humanresourcedemo.service.login;

import com.lql.humanresourcedemo.dto.model.EmployeeDTO;
import com.lql.humanresourcedemo.dto.request.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.exception.model.login.EmailNotFoundException;
import com.lql.humanresourcedemo.exception.model.login.WrongPasswordException;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import com.lql.humanresourcedemo.service.jwt.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class LoginService {
    private final EmployeeService employeeService;
    private final JWTService jwtService;
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
