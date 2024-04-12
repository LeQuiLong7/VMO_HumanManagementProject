package com.lql.humanresourcedemo.exception.model.employee;

public  class EmployeeException extends RuntimeException{
    public EmployeeException(String message) {
        super(message);
    }
    public EmployeeException(Long employeeId) {
        super("Could not find employee " + employeeId);
    }
}
