package com.lql.humanresourcedemo.service.login;

import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.dto.request.employee.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.service.employee.EmployeeServiceImpl;
import com.lql.humanresourcedemo.service.jwt.JWTServiceImpl;
import com.lql.humanresourcedemo.service.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor

public class LoginServiceImpl implements LoginService {
    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        OnlyIdPasswordAndRole employee = employeeRepository.findByEmail(loginRequest.email(), OnlyIdPasswordAndRole.class)
                .orElseThrow(() -> new LoginException("%s doesn't exists".formatted(loginRequest.email())));

        if (!passwordEncoder.matches(loginRequest.password(), employee.password())) {
            throw new LoginException("password %s is not correct for %s".formatted(loginRequest.password(), loginRequest.email()));
        }

        return new LoginResponse(jwtService.generateToken(employee));

    }

    ;
}
