package com.lql.humanresourcedemo.service.login;

import com.lql.humanresourcedemo.constant.JWTConstants;
import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.jwt.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class LoginServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private RedisTemplate<Long, String> redisTemplate;
    private LoginService loginService;

    @BeforeEach
    void setUp() {
        loginService = new LoginServiceImpl(employeeRepository, redisTemplate, jwtService, passwordEncoder, 5, "MINUTES");
    }

    @Test
    void testLogin_Successful() {
        LoginRequest loginRequest = new LoginRequest("example@example.com", "password");
        OnlyIdPasswordAndRole employee = new OnlyIdPasswordAndRole(1L, "password", Role.ADMIN);
        String mockToken = "token";

        when(employeeRepository.findByEmail(loginRequest.email(), OnlyIdPasswordAndRole.class))
                .thenReturn(Optional.of(employee));

        when(passwordEncoder.matches(loginRequest.password(), employee.password())).thenReturn(true);

        when(jwtService.generateToken(employee)).thenReturn(mockToken);
        when(redisTemplate.opsForValue()).thenReturn(Mockito.mock(ValueOperations.class));


        LoginResponse loginResponse = loginService.login(loginRequest);

        assertEquals(JWTConstants.TOKEN_TYPE, loginResponse.type());
        assertEquals(mockToken, loginResponse.token());
        assertEquals(Role.ADMIN, loginResponse.role());
    }


    @Test
    void testLogin_UserNotFound() {

        LoginRequest loginRequest = new LoginRequest("nonexistent@example.com", "password");
        when(employeeRepository.findByEmail(loginRequest.email(), OnlyIdPasswordAndRole.class)).thenReturn(Optional.empty());

        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(loginRequest));
        assertEquals("%s doesn't exists".formatted(loginRequest.email()), exception.getMessage());
    }

    @Test
    void testLogin_IncorrectPassword() {
        LoginRequest loginRequest = new LoginRequest("example@example.com", "wrongPassword");
        OnlyIdPasswordAndRole employee = new OnlyIdPasswordAndRole(1L, "password", Role.ADMIN);

        when(employeeRepository.findByEmail(loginRequest.email(), OnlyIdPasswordAndRole.class)).thenReturn(Optional.of(employee));

        when(passwordEncoder.matches(loginRequest.password(), employee.password())).thenReturn(false);

        LoginException exception = assertThrows(LoginException.class, () -> loginService.login(loginRequest));
        assertEquals("Password is not correct", exception.getMessage());
    }
}