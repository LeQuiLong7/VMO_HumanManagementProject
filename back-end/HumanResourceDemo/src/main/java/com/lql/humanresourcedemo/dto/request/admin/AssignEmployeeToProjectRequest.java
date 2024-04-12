package com.lql.humanresourcedemo.dto.request.admin;

import java.util.List;

public record AssignEmployeeToProjectRequest (Long projectId, List<Long> employeeIds){
}
