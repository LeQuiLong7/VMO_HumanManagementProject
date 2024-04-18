package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;


    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping
    public Page<LeaveResponse> getAllLeaveRequest(@RequestParam(required = false, defaultValue = "0") String page,
                                                  @RequestParam(required = false, defaultValue = "10") String size,
                                                  @RequestParam(required = false) List<String> p,
                                                  @RequestParam(required = false) List<String> o) {
        return leaveService.getAllLeaveRequest(getCurrentEmployeeId(), page, size, p, o);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public LeaveResponse createLeaveRequest(@RequestBody LeaveRequestt leaveRequestt) {
        return leaveService.createLeaveRequest(getCurrentEmployeeId(), leaveRequestt);
    }

}
