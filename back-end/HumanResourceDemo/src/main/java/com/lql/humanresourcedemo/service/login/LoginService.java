package com.lql.humanresourcedemo.service.login;

import com.lql.humanresourcedemo.dto.request.employee.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;

public interface LoginService {
    LoginResponse login(LoginRequest loginRequest);
}
