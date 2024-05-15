package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.service.pm.PMService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;

@RestController
@RequestMapping("/pm")
@PreAuthorize("hasRole('PM')")
@RequiredArgsConstructor
@Tag(name="7. PM controller")
public class PMController {

    private final PMService pmService;
    @GetMapping("/employees")
    public Page<GetProfileResponse> getAllEmployee(Pageable page) {
        return pmService.getAllEmployee(getCurrentEmployeeId(), page);
    }


    @GetMapping("/leave")
    public Page<LeaveResponse> getAllLeaveRequest(@SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable page) {
        return pmService.getAllLeaveRequest(getCurrentEmployeeId(),  page);
    }


    @PostMapping("/attendance")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Attendance> checkAttendance(@RequestBody CheckAttendanceRequest request) {
        return pmService.checkAttendance(getCurrentEmployeeId(), request);
    }
    @PutMapping("/leave")
    public List<LeaveResponse> handleLeaveRequest(@RequestBody List<HandleLeaveRequest> request) {
        return pmService.handleLeaveRequest(getCurrentEmployeeId(), request);
    }
}
