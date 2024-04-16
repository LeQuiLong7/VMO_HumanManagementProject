package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface LeaveService {
    LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt request);

    Page<LeaveResponse> getAllLeaveRequest(Long employeeId, String page, String pageSize, List<String> properties, List<String> orders);
}
