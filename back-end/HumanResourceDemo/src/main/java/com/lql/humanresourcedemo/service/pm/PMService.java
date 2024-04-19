package com.lql.humanresourcedemo.service.pm;

import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PMService {
    List<Attendance> checkAttendance(Long pmId, CheckAttendanceRequest request);

    List<LeaveResponse> handleLeaveRequest(Long pmId, List<HandleLeaveRequest> request);

    Page<GetProfileResponse> getAllEmployee(Long pmId, String page, String pageSize, List<String> properties, List<String> orders);
    Page<LeaveResponse> getAllLeaveRequest(Long pmId, String page, String pageSize, List<String> properties, List<String> orders);
}
