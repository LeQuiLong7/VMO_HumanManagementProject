package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.service.pm.PMService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pm")
@PreAuthorize("hasRole('PM')")
@RequiredArgsConstructor
public class PMController {

    private final PMService pmService;

    @PostMapping("/attendance")
    public Object checkAttendance(@RequestBody CheckAttendanceRequest request) {
        return pmService.checkAttendance(request);
    }
    @PostMapping("/leave")
    public LeaveResponse handleLeaveRequest(@RequestBody HandleLeaveRequest request) {
        return pmService.handleLeaveRequest(request);
    }
}
