package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveService {


    LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt leaveRequestt);

    Page<LeaveResponse> getAllLeaveRequest(Long employeeId, Role role, Pageable pageRequest);

    Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(Long employeeId, LocalDate date);
    LeaveResponse handleLeaveRequest(Long pmId, HandleLeaveRequest request);
}
