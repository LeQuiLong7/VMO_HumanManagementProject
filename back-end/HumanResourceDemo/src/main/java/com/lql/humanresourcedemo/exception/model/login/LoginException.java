package com.lql.humanresourcedemo.exception.model.login;

import org.springframework.security.core.context.SecurityContextHolder;

public abstract class LoginException extends RuntimeException{
    public LoginException(String message) {

        super(message);
    }
}
