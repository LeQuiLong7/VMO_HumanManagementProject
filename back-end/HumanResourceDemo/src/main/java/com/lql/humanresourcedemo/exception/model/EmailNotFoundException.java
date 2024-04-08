package com.lql.humanresourcedemo.exception.model;

public class EmailNotFoundException extends LoginException{
    public EmailNotFoundException(String email) {

        super(String.format("%s doesn't exists", email));
    }
}
