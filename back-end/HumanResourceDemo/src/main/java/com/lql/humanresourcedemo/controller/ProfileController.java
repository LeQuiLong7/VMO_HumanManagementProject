package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.effort.EffortHistoryRecord;
import com.lql.humanresourcedemo.dto.response.employee.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.lql.humanresourcedemo.util.ContextUtil.getCurrentEmployeeId;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
@Slf4j
@Tag(name="3. Profile controller")
public class ProfileController {

    private final EmployeeService employeeService;

    @GetMapping
    public GetProfileResponse getProfile() {
        return employeeService.getProfile(getCurrentEmployeeId());
    }
    @GetMapping("/effort")
    public List<EffortHistoryRecord> getEffortHistory(@RequestParam(required = false, defaultValue = "false") boolean year) {
        return employeeService.getEffortHistory(getCurrentEmployeeId(), LocalDate.now(), year);
    }

    @GetMapping("/leave")
    public Page<LeaveResponse> getAllLeaveRequest(Pageable page) {
        return employeeService.getAllLeaveRequest(getCurrentEmployeeId(), page);
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
