package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;


    @PreAuthorize("hasAnyRole({'EMPLOYEE', 'PM'})")
    @PostMapping
    public SalaryRaiseResponse createSalaryRaiseRequest(@RequestBody CreateSalaryRaiseRequest createSalaryRaiseRequest) {
        return employeeService.createSalaryRaiseRequest(getCurrentEmployeeId(), createSalaryRaiseRequest);
    }


}
