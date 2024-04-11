package com.lql.humanresourcedemo.dto.request.employee;

public record CreateSalaryRaiseRequest (Long employeeId, Double expectedSalary, String description){
}
