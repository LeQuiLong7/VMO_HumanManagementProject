package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.model.project.EmployeeProject;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.List;

public record AssignEmployeeToProjectRequest (@NotNull Long projectId, @NotNull @NotEmpty List<EmployeeEffort> assignList){
        public static record EmployeeEffort(Long employeeId, Integer effort){
            public static EmployeeEffort toEmployeeEffort(EmployeeProject ep) {
                return new EmployeeEffort(ep.getId().getEmployeeId(), ep.getEffort());
            }
        }
}

