package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.model.project.EmployeeProject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignEmployeeToProjectRequest (
        @NotNull (message = "Project id must not be null")
        Long projectId,
        @NotNull (message = "Assign list must not be null")
        @NotEmpty (message = "Assign list must not be empty")
        List<EmployeeEffort> assignList){
        public static record EmployeeEffort(
                @NotNull(message = "Employee id must not be null")
                Long employeeId,
                @Min(value = 0, message = "Effort must be greater than 0")
                @Max(value = 100, message = "Effort must be smaller than 100")
                @NotNull(message = "Effort must not be null")
                Integer effort){
            public static EmployeeEffort toEmployeeEffort(EmployeeProject ep) {
                return new EmployeeEffort(ep.getId().getEmployeeId(), ep.getEffort());
            }
        }
}

