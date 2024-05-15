package com.lql.humanresourcedemo.dto.request.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LoginRequest(
        @NotBlank(message = "Email must not be blank")
        @NotNull(message = "Email must not be null")
        String email,
        @NotNull(message = "password must not be null")
        String password) {
}
