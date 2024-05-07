package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.dto.request.employee.CreateResetPasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.ResetPasswordRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.service.password.PasswordService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reset-password")
@RequiredArgsConstructor
@Tag(name="2. Reset password controller")
public class ResetPasswordController {

    private final PasswordService passwordService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChangePasswordResponse createResetPasswordRequest(@RequestBody @Valid CreateResetPasswordRequest request) {
        return passwordService.createPasswordResetRequest(request.email());
    }
    @PutMapping
    public ChangePasswordResponse performResetPassword(@RequestBody @Valid  ResetPasswordRequest request) {
        return passwordService.resetPassword(request);
    }
}
