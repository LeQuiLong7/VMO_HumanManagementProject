package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.Min;

public record CreateSalaryRaiseRequest (@Min(0) Double expectedSalary, String description){
}
