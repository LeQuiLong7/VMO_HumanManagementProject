package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.model.project.EmployeeProject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record AssignEmployeeToProjectRequest (@NotNull Long projectId, @NotNull @NotEmpty List<EmployeeEffort> assignList){
        public static record EmployeeEffort(@NotNull Long employeeId, @Min(0) @Max(100) Integer effort){
            public static EmployeeEffort toEmployeeEffort(EmployeeProject ep) {
                return new EmployeeEffort(ep.getId().getEmployeeId(), ep.getEffort());
            }
        }
}

