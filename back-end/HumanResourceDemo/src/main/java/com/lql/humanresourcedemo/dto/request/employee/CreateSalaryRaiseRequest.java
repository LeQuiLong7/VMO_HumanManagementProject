package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateSalaryRaiseRequest (
        @Min(value = 0, message = "Expected salary must be greater than 0")
        @NotNull(message = "Expected salary must not be null")
        Double expectedSalary,
        String description){
}
