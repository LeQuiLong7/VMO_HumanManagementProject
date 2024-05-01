package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final EmployeeService employeeService;

    @GetMapping
    public GetProfileResponse getProfile() {
        return employeeService.getProfile(getCurrentEmployeeId());
    }

    @GetMapping("/tech")
    public TechStackResponse getTechStack() {
        return employeeService.getTechStack(getCurrentEmployeeId());
    }

    @PutMapping
    public GetProfileResponse updateProfile(@RequestBody @Valid UpdateProfileRequest updateProfileRequest) {
        return employeeService.updateInfo(getCurrentEmployeeId(), updateProfileRequest);
    }

    @PutMapping("/password")
    public ChangePasswordResponse changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) {
        return employeeService.changePassword(getCurrentEmployeeId(), changePasswordRequest);
    }

    @PutMapping("/avatar")
    public String uploadAvatar(MultipartFile file) {
        return employeeService.uploadAvatar(getCurrentEmployeeId(), file);
    }
}
