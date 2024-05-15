package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HandleSalaryRaiseRequest(
        @NotNull(message = "Request id must not be null")
        Long requestId,
        @NotNull (message = "Status must not be null")
        SalaryRaiseRequestStatus status,
        @Min(value = 0, message = "New salary must be greater than 0")
        @NotNull (message = "New salary must not be null")
        Double newSalary) {
}
