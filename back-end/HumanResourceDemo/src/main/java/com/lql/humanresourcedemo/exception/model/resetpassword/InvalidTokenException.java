package com.lql.humanresourcedemo.exception.model.resetpassword;

public class InvalidTokenException extends ResetPasswordException{
    public InvalidTokenException(String message) {
        super(message);
    }
}

