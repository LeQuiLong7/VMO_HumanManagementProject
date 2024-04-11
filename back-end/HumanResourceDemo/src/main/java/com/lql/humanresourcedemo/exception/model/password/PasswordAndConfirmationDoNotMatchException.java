package com.lql.humanresourcedemo.exception.model.password;

public class PasswordAndConfirmationDoNotMatchException extends PasswordException{
    public PasswordAndConfirmationDoNotMatchException(Long accountId) {
        super("Password and confirmation password do not match - AccountId: " + accountId);
    }
    public PasswordAndConfirmationDoNotMatchException(String msg) {
        super(msg);
    }
}
