package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.service.pm.PMService;
import com.lql.humanresourcedemo.utility.ContextUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pm")
@PreAuthorize("hasRole('PM')")
@RequiredArgsConstructor
public class PMController {

    private final PMService pmService;
    @GetMapping("/employees")
    public Page<GetProfileResponse> getAllEmployee(@RequestParam(required = false, defaultValue = "0") String page,
                                                   @RequestParam(required = false, defaultValue = "10") String size,
                                                   @RequestParam(required = false) List<String> p,
                                                   @RequestParam(required = false) List<String> o) {
        return pmService.getAllEmployee(ContextUtility.getCurrentEmployeeId(), page, size, p, o);
    }


    @GetMapping("/leave")
    public Page<LeaveResponse> getAllLeaveRequest(@RequestParam(required = false, defaultValue = "0") String page,
                                                   @RequestParam(required = false, defaultValue = "10") String size,
                                                   @RequestParam(required = false) List<String> p,
                                                   @RequestParam(required = false) List<String> o) {
        return pmService.getAllLeaveRequest(ContextUtility.getCurrentEmployeeId(), page, size, p, o);
    }


    @PostMapping("/attendance")
    public List<Attendance> checkAttendance(@RequestBody CheckAttendanceRequest request) {
        return pmService.checkAttendance(ContextUtility.getCurrentEmployeeId(), request);
    }
    @PutMapping("/leave")
    public List<LeaveResponse> handleLeaveRequest(@RequestBody List<HandleLeaveRequest> request) {
        return pmService.handleLeaveRequest(ContextUtility.getCurrentEmployeeId(), request);
    }
}
