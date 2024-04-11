package com.lql.humanresourcedemo.dto.request.pm;

import com.lql.humanresourcedemo.enumeration.LeaveStatus;

public record HandleLeaveRequest(Long requestId, LeaveStatus status) {
}
