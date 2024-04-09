package com.lql.humanresourcedemo.exception.model.employee;

public abstract class EmployeeException extends RuntimeException{
    public EmployeeException(String message) {
        super(message);
    }
}
