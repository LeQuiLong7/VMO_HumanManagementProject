package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.security.MyAuthentication;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final EmployeeService employeeService;


    @GetMapping
    public GetProfileResponse getProfile() {
        return employeeService.findById(((MyAuthentication) SecurityContextHolder.getContext().getAuthentication()).getEmployeeId(), GetProfileResponse.class);
    }

    @GetMapping("/tech")
    public TechStackResponse getTechStack() {
        return employeeService.getTechStack(((MyAuthentication) SecurityContextHolder.getContext().getAuthentication()).getEmployeeId());
    }

    @PutMapping
    public GetProfileResponse updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest) {
        return employeeService.updateInfo(updateProfileRequest);
    }

    @PutMapping("/password")
    public ResponseEntity<ChangePasswordResponse> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return employeeService.changePassword(changePasswordRequest);
    }
}
