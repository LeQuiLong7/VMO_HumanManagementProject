package com.lql.humanresourcedemo.exception.model;

public class WrongPasswordException extends LoginException{
    public WrongPasswordException(String email, String password) {

        super(String.format("password %s is not correct for %s", password, email));
    }
}
