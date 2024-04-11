package com.lql.humanresourcedemo.exception.model.resetpassword;

public class NewPasswordNotMatchException extends ResetPasswordException{
    public NewPasswordNotMatchException(String message) {
        super(message);
    }
}
