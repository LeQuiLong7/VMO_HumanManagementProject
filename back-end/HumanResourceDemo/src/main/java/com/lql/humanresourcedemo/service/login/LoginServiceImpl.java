package com.lql.humanresourcedemo.service.login;

import com.lql.humanresourcedemo.constant.JWTConstants;
import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.response.login.LoginResponse;
import com.lql.humanresourcedemo.dto.response.login.LogoutResponse;
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
    private final RedisTemplate<String,  LoginResponse> redisTemplate2;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final long EXPIRED_DURATION;
    private final String EXPIRED_TIME_UNIT;

    public LoginServiceImpl(EmployeeRepository employeeRepository,
                            RedisTemplate<Long, String> redisTemplate,
                            RedisTemplate<String, LoginResponse> redisTemplate2,
                            JwtService jwtService,
                            PasswordEncoder passwordEncoder,
                            @Value("${jwt.expiration.duration}") final long EXPIRED_DURATION,
                            @Value("${jwt.expiration.time-unit}") final String EXPIRED_TIME_UNIT) {
        this.employeeRepository = employeeRepository;
        this.redisTemplate = redisTemplate;
        this.redisTemplate2 = redisTemplate2;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.EXPIRED_DURATION = EXPIRED_DURATION;
        this.EXPIRED_TIME_UNIT = EXPIRED_TIME_UNIT;
    }

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // get only the password and role for the email provided in the request if exists
        OnlyIdPasswordAndRole employee = employeeRepository.findByEmail(loginRequest.email(), OnlyIdPasswordAndRole.class)
                .orElseThrow(() -> new LoginException("%s doesn't exists".formatted(loginRequest.email())));

        // check if the login password and the account's password matches or not
        if (!passwordEncoder.matches(loginRequest.password(), employee.password())) {
            throw new LoginException("Password is not correct");
        }
        // password matches, generate a jwt token
        String token = jwtService.generateToken(employee);

        // store the token in redis, key is the employee id
        redisTemplate.opsForValue().set(employee.id(), token, Duration.of(EXPIRED_DURATION, ChronoUnit.valueOf(EXPIRED_TIME_UNIT)));
        return new LoginResponse(JWTConstants.TOKEN_TYPE, token, employee.role());
    }

    @Override
    public LoginResponse exchangeToken(String token) {
        // check if the token exists or not
        LoginResponse loginResponse = redisTemplate2.opsForValue().get(token);
        if(loginResponse == null) {
            throw new LoginException("Session id does not exists");
        }
        return loginResponse;
    }

    @Override
    // login with oauth2, success if the email exists in the system and fail if not, no need for password
    public LoginResponse loginWithOauth2(String email) {

        OnlyIdPasswordAndRole employee = employeeRepository.findByPersonalEmail(email, OnlyIdPasswordAndRole.class)
                .orElseThrow(() -> new LoginException("Account doesn't exists"));
        // generate the jwt token
        String token = jwtService.generateToken(employee);
        // store the token in redis, the key is employee id
        redisTemplate.opsForValue().set(employee.id(), token, Duration.of(EXPIRED_DURATION, ChronoUnit.valueOf(EXPIRED_TIME_UNIT)));
        return new LoginResponse(JWTConstants.TOKEN_TYPE, token, employee.role());
    }

    @Override
    // delete the record for the token from redis when performing logout
    public LogoutResponse logout(String token) {
        try {
            // get the employee id from the token
            long employeeId = Long.parseLong(jwtService.extractClaim(token, Claims::getSubject));
            // check if exists any token corresponding to the employee id and if the two token matches or not
            String storedToken = redisTemplate.opsForValue().get(employeeId);
            if (storedToken == null) {
                return new LogoutResponse(false, "You haven't logged in yet");
            }
            if (!storedToken.equals(token)) {
                return new LogoutResponse(false, "Invalid token");
            }
            // check successfully, delete the record
            redisTemplate.delete(employeeId);
            return new LogoutResponse(true, "Logout successful!");
        } catch (JwtException ex) {
            return new LogoutResponse(false, "Token not valid");
        }
    }
}
