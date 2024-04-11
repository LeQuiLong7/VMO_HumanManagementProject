package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.lql.humanresourcedemo.utility.ContextUtility.*;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final EmployeeService employeeService;


    @GetMapping
    public GetProfileResponse getProfile() {
        return employeeService.findById(getCurrentEmployeeId(), GetProfileResponse.class);
    }

    @GetMapping("/tech")
    public TechStackResponse getTechStack() {
        return employeeService.getTechStack(getCurrentEmployeeId());
    }

    @PutMapping
    public GetProfileResponse updateProfile(@RequestBody UpdateProfileRequest updateProfileRequest) {
        return employeeService.updateInfo(updateProfileRequest);
    }

    @PutMapping("/password")
    public ChangePasswordResponse changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        return employeeService.changePassword(changePasswordRequest);
    }

    @PutMapping("/avatar")
    public String uploadAvatar(MultipartFile file) {
        return employeeService.uploadAvatar(file, getCurrentEmployeeId());
    }
}
