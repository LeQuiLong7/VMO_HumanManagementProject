package com.lql.humanresourcedemo.dto.response.leave;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record LeaveResponse(Long id, Long employeeId, String employeeName, String avatarUrl, LocalDate date, LocalDateTime createdAt, LeaveType type, String reason, LeaveStatus status, Long approvedBy){
}
