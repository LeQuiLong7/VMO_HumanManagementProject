package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.NotNull;

public record ChangePasswordRequest(
        @NotNull String oldPassword,
        @NotNull String newPassword,
        @NotNull String confirmPassword
) {
}
