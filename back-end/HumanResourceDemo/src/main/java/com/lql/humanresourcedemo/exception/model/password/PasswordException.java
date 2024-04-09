package com.lql.humanresourcedemo.exception.model.password;

public abstract class PasswordException extends RuntimeException{
    public PasswordException(String message) {
        super(message);
    }
}
