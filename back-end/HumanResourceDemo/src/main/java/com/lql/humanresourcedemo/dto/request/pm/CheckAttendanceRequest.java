package com.lql.humanresourcedemo.dto.request.pm;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;
import java.util.List;

public record CheckAttendanceRequest (
        @NotNull(message = "Attendance list must not be null")
        @NotEmpty(message = "Attendance list must not be empty")
        List<AttendanceDetail> attendanceDetails) {


    public record AttendanceDetail(
            @NotNull(message = "Employee id must not be null")
            Long employeeId,
            LocalTime timeIn,
            LocalTime timeOut){}

}
