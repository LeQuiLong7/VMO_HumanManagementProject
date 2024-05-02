package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.response.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;
import static com.lql.humanresourcedemo.utility.HelperUtility.validateAndBuildPageRequest;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @PostMapping("/salary")
    public SalaryRaiseResponse createSalaryRaiseRequest(@RequestBody @Valid CreateSalaryRaiseRequest createSalaryRaiseRequest) {
        return employeeService.createSalaryRaiseRequest(getCurrentEmployeeId(), createSalaryRaiseRequest);
    }


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @GetMapping("/salary")
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(@RequestParam(required = false, defaultValue = "0") String page,
                                                              @RequestParam(required = false, defaultValue = "10") String size,
                                                              @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                              @RequestParam(required = false, defaultValue = "desc") List<String> o) {
        return employeeService.getAllSalaryRaiseRequest(getCurrentEmployeeId(), validateAndBuildPageRequest(page, size, p, o, SalaryRaiseRequest.class));
    }

    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM', 'ADMIN'})")
    @GetMapping("/project")
    public Page<ProjectDetail> getAllProjectsByEmployeeId(@RequestParam(required = false, defaultValue = "0") String page,
                                                          @RequestParam(required = false, defaultValue = "10") String size,
                                                          @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                          @RequestParam(required = false, defaultValue = "desc") List<String> o) {

        return employeeService.getAllProjects(getCurrentEmployeeId(), validateAndBuildPageRequest(page, size, p, o, SalaryRaiseRequest.class));
    }


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @GetMapping("/attendance")
    public Page<Attendance> getAttendanceHistoryByEmployeeId(@RequestParam(required = false, defaultValue = "0") String page,
                                                             @RequestParam(required = false, defaultValue = "10") String size,
                                                             @RequestParam(required = false, defaultValue = "date") List<String> p,
                                                             @RequestParam(required = false, defaultValue = "desc") List<String> o) {
        return employeeService.getAllAttendanceHistory(getCurrentEmployeeId(), validateAndBuildPageRequest(page, size, p, o, Attendance.class));

    }
}
