package com.lql.humanresourcedemo.dto.request.pm;

import java.time.LocalTime;
import java.util.List;

public record CheckAttendanceRequest (List<AttendanceDetail> attendanceDetails) {


    public record AttendanceDetail(Long employeeId, LocalTime timeIn, LocalTime timeOut){}

}
