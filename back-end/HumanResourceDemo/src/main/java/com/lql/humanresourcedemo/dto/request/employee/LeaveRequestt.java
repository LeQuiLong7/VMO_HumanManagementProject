package com.lql.humanresourcedemo.dto.request.employee;

import com.lql.humanresourcedemo.enumeration.LeaveType;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record LeaveRequestt(
        @Future(message = "Leave date must be a future date")
        @NotNull(message = "Leave date must be null")
        LocalDate leaveDate,
        String reason,
        @NotNull(message = "Leave type must be not be null")
        LeaveType type
) {
}
