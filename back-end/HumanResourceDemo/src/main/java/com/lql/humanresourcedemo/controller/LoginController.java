package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.employee.LoginRequest;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.service.login.LoginService;
import com.lql.humanresourcedemo.service.schedule.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final ScheduleService scheduleService;

    @PostMapping
    public LoginResponse login(@RequestBody @Valid LoginRequest loginRequest) {
        return loginService.login(loginRequest);
    }

    @GetMapping
    public String test() {
        scheduleService.createEmployeeWeeklyReports();
        return "Hello";
    }


    @GetMapping("/employees")
    public Object getAllEmployee(@RequestParam(required = false, defaultValue = "0") Integer page,
                                 @RequestParam(required = false, defaultValue = "10") Integer size,
                                 @RequestParam(required = false) List<String> property,
                                 @RequestParam(required = false) List<Sort.Direction> order) {
        return "";
    }


}
