package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.service.pm.PMService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;
import static com.lql.humanresourcedemo.utility.HelperUtility.validateAndBuildPageRequest;

@RestController
@RequestMapping("/pm")
@PreAuthorize("hasRole('PM')")
@RequiredArgsConstructor
@Tag(name="7. PM controller")
public class PMController {

    private final PMService pmService;
    @GetMapping("/employees")
    public Page<GetProfileResponse> getAllEmployee(@RequestParam(required = false, defaultValue = "0") String page,
                                                   @RequestParam(required = false, defaultValue = "10") String size,
                                                   @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                   @RequestParam(required = false, defaultValue = "asc") List<String> o) {
        return pmService.getAllEmployee(getCurrentEmployeeId(), validateAndBuildPageRequest(page, size, p, o, Employee.class));
    }


    @GetMapping("/leave")
    public Page<LeaveResponse> getAllLeaveRequest(@RequestParam(required = false, defaultValue = "0") String page,
                                                   @RequestParam(required = false, defaultValue = "10") String size,
                                                   @RequestParam(required = false, defaultValue = "createdAt") List<String> p,
                                                   @RequestParam(required = false, defaultValue = "desc") List<String> o) {
        return pmService.getAllLeaveRequest(getCurrentEmployeeId(),  validateAndBuildPageRequest(page, size, p, o, LeaveRequest.class));
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
