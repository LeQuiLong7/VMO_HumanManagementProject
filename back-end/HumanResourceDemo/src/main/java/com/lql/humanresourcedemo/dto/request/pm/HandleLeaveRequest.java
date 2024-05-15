package com.lql.humanresourcedemo.dto.request.pm;

import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import jakarta.validation.constraints.NotNull;

public record HandleLeaveRequest(
        @NotNull(message = "Leave request id must not be null")
        Long requestId,
        @NotNull(message = "New status must not be null")
        LeaveStatus status) {
}
