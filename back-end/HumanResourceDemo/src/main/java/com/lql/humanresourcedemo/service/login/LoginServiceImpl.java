package com.lql.humanresourcedemo.service.login;

import com.lql.humanresourcedemo.constant.JWTConstants;
import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.dto.response.LogoutResponse;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.jwt.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;


@Service
public class LoginServiceImpl implements LoginService {
    private final EmployeeRepository employeeRepository;
    private final RedisTemplate<Long, String> redisTemplate;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final long EXPIRED_DURATION;
    private final String EXPIRED_TIME_UNIT;

    public LoginServiceImpl(EmployeeRepository employeeRepository,
                            RedisTemplate<Long, String> redisTemplate,
                            JwtService jwtService,
                            PasswordEncoder passwordEncoder,
                            @Value("${jwt.expiration.duration}") long EXPIRED_DURATION,
                            @Value("${jwt.expiration.time-unit}") String EXPIRED_TIME_UNIT) {
        this.employeeRepository = employeeRepository;
        this.redisTemplate = redisTemplate;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.EXPIRED_DURATION = EXPIRED_DURATION;
        this.EXPIRED_TIME_UNIT = EXPIRED_TIME_UNIT;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {

        OnlyIdPasswordAndRole employee = employeeRepository.findByEmail(loginRequest.email(), OnlyIdPasswordAndRole.class)
                .orElseThrow(() -> new LoginException("%s doesn't exists".formatted(loginRequest.email())));

        if (!passwordEncoder.matches(loginRequest.password(), employee.password())) {
            throw new LoginException("Password is not correct");
        }

        String token = jwtService.generateToken(employee);
        redisTemplate.opsForValue().set(employee.id(), token, Duration.of(EXPIRED_DURATION, ChronoUnit.valueOf(EXPIRED_TIME_UNIT)));
        return new LoginResponse(JWTConstants.TOKEN_TYPE, token, employee.role());
    }

    @Override
    public LogoutResponse logout(String token) {
        try {
            long employeeId = Long.parseLong(jwtService.extractClaim(token, Claims::getSubject));
            String storedToken = redisTemplate.opsForValue().get(employeeId);
            if (storedToken == null) {
                return new LogoutResponse(false, "You haven't logged in yet");
            }
            if (!storedToken.equals(token)) {
                return new LogoutResponse(false, "Invalid token");
            }
            redisTemplate.delete(employeeId);
            return new LogoutResponse(true, "Logout successful!");
        } catch (JwtException ex) {
            return new LogoutResponse(false, "Token not valid");
        }
    }
}
