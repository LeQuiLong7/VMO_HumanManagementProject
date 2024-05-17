package com.lql.humanresourcedemo.service.login;

import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.response.login.LoginResponse;
import com.lql.humanresourcedemo.dto.response.login.LogoutResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);
    LoginResponse exchangeToken(String token);
    LoginResponse loginWithOauth2(String email);
    LogoutResponse logout(String token);
}
