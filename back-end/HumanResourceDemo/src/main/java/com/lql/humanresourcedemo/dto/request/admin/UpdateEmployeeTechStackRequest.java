package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateEmployeeTechStackRequest (
        @NotNull(message = "Employee id must not be null")
        Long employeeId,
        @NotNull(message = "Tech stack must not be null")
        @NotEmpty (message = "Tech stack must not be empty")
        List<TechStack> techStacks) {

}
