package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResetPasswordRequest (
        @NotNull(message = "Token must not be null")
        @NotBlank
        String token,
        @NotNull(message = "New password must not be null")
        String newPassword,
        @NotNull(message = "Confirm password must not be null")
        String confirmPassword){
}
