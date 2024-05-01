package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;

@RestController
@RequestMapping("/leave")
@RequiredArgsConstructor
public class LeaveController {
    private final LeaveService leaveService;


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @GetMapping
    public Page<LeaveResponse> getAllLeaveRequestByEmployeeId(@RequestParam(required = false, defaultValue = "0") String page,
                                                              @RequestParam(required = false, defaultValue = "10") String size,
                                                              @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                              @RequestParam(required = false, defaultValue = "desc") List<String> o) {
        return leaveService.getAllLeaveRequest(getCurrentEmployeeId(), page, size, p, o);
    }

    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @GetMapping("/{date}")
    public Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(@PathVariable LocalDate date) {
        return leaveService.getLeaveRequestByDateAndEmployeeId(getCurrentEmployeeId(), date);
    }

    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @PostMapping
    public LeaveResponse createLeaveRequest(@RequestBody @Valid LeaveRequestt leaveRequestt) {
        return leaveService.createLeaveRequest(getCurrentEmployeeId(), leaveRequestt);
    }

}
