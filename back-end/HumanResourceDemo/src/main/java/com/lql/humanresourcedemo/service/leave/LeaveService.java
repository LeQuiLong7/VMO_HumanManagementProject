package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;

public interface LeaveService {
    LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt request);
}
