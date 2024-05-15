package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.response.project.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@Tag(name="5. Employee controller")
public class EmployeeController {

    private final EmployeeService employeeService;


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @PostMapping("/salary")
    @ResponseStatus(HttpStatus.CREATED)
    public SalaryRaiseResponse createSalaryRaiseRequest(@RequestBody @Valid CreateSalaryRaiseRequest createSalaryRaiseRequest) {
        return employeeService.createSalaryRaiseRequest(getCurrentEmployeeId(), createSalaryRaiseRequest);
    }


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @GetMapping("/salary")
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Pageable page) {
        return employeeService.getAllSalaryRaiseRequest(getCurrentEmployeeId(), page);
    }

    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM', 'ADMIN'})")
    @GetMapping("/project")
    public Page<ProjectDetail> getAllProjectsByEmployeeId(Pageable page) {

        return employeeService.getAllProjects(getCurrentEmployeeId(), page);
    }


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @GetMapping("/attendance")
    public Page<Attendance> getAttendanceHistoryByEmployeeId(@SortDefault(sort = "date", direction = Sort.Direction.DESC) Pageable page) {
        return employeeService.getAllAttendanceHistory(getCurrentEmployeeId(), page);

    }
}
