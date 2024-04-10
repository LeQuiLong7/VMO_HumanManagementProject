package com.lql.humanresourcedemo.dto.request;

public record CreateSalaryRaiseRequest (Long employeeId, Double expectedSalary, String description){
}
