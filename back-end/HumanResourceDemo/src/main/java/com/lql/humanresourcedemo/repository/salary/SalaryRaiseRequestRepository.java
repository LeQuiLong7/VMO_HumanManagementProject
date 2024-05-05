package com.lql.humanresourcedemo.repository.salary;

import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SalaryRaiseRequestRepository extends JpaRepository<SalaryRaiseRequest, Long>, JpaSpecificationExecutor<SalaryRaiseRequest> {
//    Page<SalaryRaiseRequest> findAllByEmployeeId(Long employeeId, Pageable pageRequest);
}
