package com.lql.humanresourcedemo.exception.model;

import org.springframework.security.core.context.SecurityContextHolder;

public abstract class LoginException extends RuntimeException{
    public LoginException(String message) {

        super(message +  " - IP Address: " + SecurityContextHolder.getContext().getAuthentication().getName());
    }
}
