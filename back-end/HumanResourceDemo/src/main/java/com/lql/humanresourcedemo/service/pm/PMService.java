package com.lql.humanresourcedemo.service.pm;

import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PMService {
    List<Attendance> checkAttendance(Long pmId, CheckAttendanceRequest request);
    List<LeaveResponse> handleLeaveRequest(Long pmId, List<HandleLeaveRequest> request);
    Page<GetProfileResponse> getAllEmployee(Long pmId, Pageable pageRequest);
    Page<LeaveResponse> getAllLeaveRequest(Long pmId, Pageable pageRequest);
}
