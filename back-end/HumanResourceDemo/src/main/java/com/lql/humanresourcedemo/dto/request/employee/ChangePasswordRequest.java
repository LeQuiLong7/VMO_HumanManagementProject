package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequest(
        @NotNull (message = "Old password must not be null")
        String oldPassword,
        @NotNull (message = "New password must not be null")
        String newPassword,
        @NotNull (message = "Confirm password must not be null")
        String confirmPassword
) {
}
