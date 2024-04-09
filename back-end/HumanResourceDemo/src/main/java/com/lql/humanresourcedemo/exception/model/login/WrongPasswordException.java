package com.lql.humanresourcedemo.exception.model.login;

import com.lql.humanresourcedemo.exception.model.login.LoginException;

public class WrongPasswordException extends LoginException {
    public WrongPasswordException(String email, String password) {

        super(String.format("password %s is not correct for %s", password, email));
    }
}
