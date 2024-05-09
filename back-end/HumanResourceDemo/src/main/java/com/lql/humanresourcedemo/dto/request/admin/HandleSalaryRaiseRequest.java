package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record HandleSalaryRaiseRequest(
        @NotNull Long requestId,
        @NotNull SalaryRaiseRequestStatus status,
        @Min(0) Double newSalary) {
}
