package com.lql.humanresourcedemo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;

import java.time.LocalDateTime;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalaryRaiseResponse (
        Long employeeId,
        Double expectedSalary,
        String description,
        SalaryRaiseRequestStatus status,
        LocalDateTime createdAt,
        Long createdBy,
        Double newSalary,
        Long approvedBy
){
}