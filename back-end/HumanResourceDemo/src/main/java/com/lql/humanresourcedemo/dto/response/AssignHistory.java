package com.lql.humanresourcedemo.dto.response;

import com.lql.humanresourcedemo.enumeration.Role;

import java.time.LocalDateTime;

public record AssignHistory(Long employeeId, String employeeName, String avatarUrl, Role role, LocalDateTime assignAt, Long assignBy) {
}
