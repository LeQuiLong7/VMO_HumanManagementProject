package com.lql.humanresourcedemo.dto.request.employee;

import com.lql.humanresourcedemo.enumeration.LeaveType;

import java.time.LocalDate;

public record LeaveRequestt(Long employeeId, LocalDate leaveDate, String reason, LeaveType type) {
}
