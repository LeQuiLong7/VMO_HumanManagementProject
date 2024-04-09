package com.lql.humanresourcedemo.exception.model.employee;

public class EmployeeNotFoundException extends EmployeeException{
    public EmployeeNotFoundException(Long employeeId) {
        super("Could not find employee " + employeeId);
    }
}
