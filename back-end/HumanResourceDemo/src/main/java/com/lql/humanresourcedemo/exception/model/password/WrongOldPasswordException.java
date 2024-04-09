package com.lql.humanresourcedemo.exception.model.password;

public class WrongOldPasswordException extends PasswordException{
    public WrongOldPasswordException(Long accountId) {
        super("Wrong old password - AccountId: " + accountId);
    }
}
