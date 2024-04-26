package com.lql.humanresourcedemo.dto.response;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.project.Project;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record ProjectDetail(
        Project projectInfo,

        List<AssignHistory> assignHistory


) {

    public record AssignHistory(Long employeeId, String employeeName, String avatarUrl, Role role,  LocalDateTime assignAt, Long assignBy){}
}
