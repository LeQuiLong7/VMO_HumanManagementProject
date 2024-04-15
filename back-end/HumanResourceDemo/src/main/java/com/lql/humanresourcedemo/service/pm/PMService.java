package com.lql.humanresourcedemo.service.pm;

import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;

import java.util.List;

public interface PMService {
    List<Attendance> checkAttendance(CheckAttendanceRequest request);

    LeaveResponse handleLeaveRequest(HandleLeaveRequest request);
}
