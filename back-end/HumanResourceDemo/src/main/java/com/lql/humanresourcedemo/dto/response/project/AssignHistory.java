package com.lql.humanresourcedemo.dto.response.project;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.project.EmployeeProject;

import java.time.LocalDateTime;

public record AssignHistory(Long employeeId, String employeeName, String avatarUrl, Role role, LocalDateTime assignAt, Long assignBy) {


    public static AssignHistory of(EmployeeProject ep) {
        return new AssignHistory(
                ep.getEmployee().getId(),
                ep.getEmployee().getLastName() + " " + ep.getEmployee().getFirstName(),
                ep.getEmployee().getAvatarUrl(),
                ep.getEmployee().getRole(),
                ep.getCreatedAt(),
                ep.getCreatedBy());
    }
}
