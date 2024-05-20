package com.lql.humanresourcedemo.service.salary;

import com.lql.humanresourcedemo.dto.request.admin.HandleSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SalaryService {
    SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request);
    Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Long employeeId, Role role, Pageable pageRequest);
    SalaryRaiseResponse handleSalaryRaiseRequest(Long adminId, HandleSalaryRaiseRequest handleRequest);


}
