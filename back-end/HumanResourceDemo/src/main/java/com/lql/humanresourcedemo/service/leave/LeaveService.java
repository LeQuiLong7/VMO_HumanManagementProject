package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;

public interface LeaveService {

    Page<LeaveResponse> getAllLeaveRequest(Long employeeId, Pageable pageRequest);

    Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(Long employeeId, LocalDate date);
}
