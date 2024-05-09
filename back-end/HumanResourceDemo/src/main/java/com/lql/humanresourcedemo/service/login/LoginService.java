package com.lql.humanresourcedemo.service.login;

import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.dto.response.LogoutResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);
    LogoutResponse logout(String token);
}
