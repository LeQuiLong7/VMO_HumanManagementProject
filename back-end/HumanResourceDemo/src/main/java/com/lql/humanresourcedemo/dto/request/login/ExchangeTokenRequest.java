package com.lql.humanresourcedemo.dto.request.login;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ExchangeTokenRequest (@NotNull @NotBlank String sessionId){
}
