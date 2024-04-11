package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;


    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public LeaveResponse createLeaveRequest(@RequestBody LeaveRequestt leaveRequestt) {
        return leaveService.createLeaveRequest(leaveRequestt);
    }

}
