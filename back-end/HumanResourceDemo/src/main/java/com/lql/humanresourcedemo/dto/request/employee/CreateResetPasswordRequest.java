package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResetPasswordRequest(
        @NotNull(message = "Email must not be null")
        @NotBlank (message = "Email must not be blank")
        String email) {
}
