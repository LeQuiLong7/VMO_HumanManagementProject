package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest (
        @NotNull
        @NotBlank
        String token,
        @NotNull
        @NotBlank
        String newPassword,
        @NotNull
        @NotBlank
        String confirmPassword){
}
