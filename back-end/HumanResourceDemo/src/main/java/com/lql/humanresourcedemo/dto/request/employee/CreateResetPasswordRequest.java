package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateResetPasswordRequest(@NotNull @NotBlank String email) {
}
