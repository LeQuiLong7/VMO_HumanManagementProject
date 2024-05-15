package com.lql.humanresourcedemo.dto.request.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
        @NotNull(message = "Token must not be null")
        @NotBlank (message = "Token must not be blank")
        String token) {
}
