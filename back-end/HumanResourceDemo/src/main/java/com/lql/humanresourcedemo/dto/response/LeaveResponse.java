package com.lql.humanresourcedemo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record LeaveResponse(Long id, Long employeeId, LocalDate date, String reason, LeaveStatus status, Long approvedBy){
}
