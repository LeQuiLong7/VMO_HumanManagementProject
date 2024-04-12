package com.lql.humanresourcedemo.exception.model.login;

import org.springframework.security.core.context.SecurityContextHolder;

public  class LoginException extends RuntimeException{
    public LoginException(String message) {

        super(message);
    }
}
