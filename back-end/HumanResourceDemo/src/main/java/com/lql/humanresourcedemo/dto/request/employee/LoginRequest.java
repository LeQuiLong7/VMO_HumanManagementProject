package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank
        @NotNull
        String email,
        @NotBlank
        @NotNull
        String password) {
}
