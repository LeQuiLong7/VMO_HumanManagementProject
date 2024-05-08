package com.lql.humanresourcedemo.dto.response.admin;

import com.lql.humanresourcedemo.model.project.EmployeeProject;

public record EmployeeProjectResponse(Long employeeId, String employeeName, Integer effort) {

    public static EmployeeProjectResponse toEmployeeProjectResponse(EmployeeProject ep) {
        return new EmployeeProjectResponse(ep.getId().getEmployeeId(), ep.getEmployee().getLastName()  + ' ' + ep.getEmployee().getFirstName(), ep.getEffort());
    }
}
