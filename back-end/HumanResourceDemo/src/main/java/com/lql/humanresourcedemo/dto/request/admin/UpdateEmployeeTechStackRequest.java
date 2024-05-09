package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateEmployeeTechStackRequest (
        @NotNull
        Long employeeId,
        @NotNull
        @NotEmpty
        List<TechStack> techStacks) {

}
