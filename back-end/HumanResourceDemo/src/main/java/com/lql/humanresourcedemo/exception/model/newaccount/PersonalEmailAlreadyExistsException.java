package com.lql.humanresourcedemo.exception.model.newaccount;

public class PersonalEmailAlreadyExistsException extends NewAccountException {
    public PersonalEmailAlreadyExistsException(String email, Long accountId) {
        super(
                String.format("Personal email %s already exists - AccountId: ", email, accountId)
        );
    }
}
