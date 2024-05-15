package com.lql.humanresourcedemo.dto.request.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExchangeTokenRequest(
        @NotNull(message = "Session id must not be null")
        @NotBlank(message = "Session id must not be blank")
        String sessionId) {
}
