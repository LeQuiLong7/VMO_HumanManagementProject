package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.employee.CreateResetPasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.ResetPasswordRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.service.password.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reset-password")
@RequiredArgsConstructor
public class ResetPasswordController {

    private final PasswordService resetPasswordService;

    @PostMapping
    public ChangePasswordResponse createResetPasswordRequest(@RequestBody CreateResetPasswordRequest request) {
        return resetPasswordService.createPasswordResetRequest(request.email());
    }
    @PutMapping
    public ChangePasswordResponse performResetPassword(@RequestBody ResetPasswordRequest request) {
        return resetPasswordService.resetPassword(request);
    }
}