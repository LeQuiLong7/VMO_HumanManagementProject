package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;

public record HandleSalaryRaiseRequest(Long requestId, SalaryRaiseRequestStatus status, Double newSalary) {
}
