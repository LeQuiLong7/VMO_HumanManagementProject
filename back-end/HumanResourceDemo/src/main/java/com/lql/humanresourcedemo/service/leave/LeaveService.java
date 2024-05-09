package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LeaveService {
    LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt request);

    Page<LeaveResponse> getAllLeaveRequest(Long employeeId, Pageable pageRequest);

    Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(Long employeeId, LocalDate date);
}
