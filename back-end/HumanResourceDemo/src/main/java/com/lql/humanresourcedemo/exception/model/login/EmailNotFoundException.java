package com.lql.humanresourcedemo.exception.model.login;

import com.lql.humanresourcedemo.exception.model.login.LoginException;

public class EmailNotFoundException extends LoginException {
    public EmailNotFoundException(String email) {

        super(String.format("%s doesn't exists", email));
    }
}
