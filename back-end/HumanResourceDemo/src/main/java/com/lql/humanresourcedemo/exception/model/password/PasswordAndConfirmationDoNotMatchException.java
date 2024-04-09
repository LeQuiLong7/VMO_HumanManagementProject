package com.lql.humanresourcedemo.exception.model.password;

public class PasswordAndConfirmationDoNotMatchException extends PasswordException{
    public PasswordAndConfirmationDoNotMatchException(String message) {
        super(message);
    }
}
