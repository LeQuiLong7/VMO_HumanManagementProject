package com.lql.humanresourcedemo.dto.request.employee;

import com.lql.humanresourcedemo.enumeration.LeaveType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LeaveRequestt(@Future @NotNull LocalDate leaveDate, String reason, @NotNull LeaveType type) {
}
