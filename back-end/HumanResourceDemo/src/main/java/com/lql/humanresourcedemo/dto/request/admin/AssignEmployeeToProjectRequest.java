package com.lql.humanresourcedemo.dto.request.admin;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Collection;
import java.util.List;

public record AssignEmployeeToProjectRequest (@NotNull Long projectId, @NotNull @NotEmpty Collection<Long> employeeIds){
}
