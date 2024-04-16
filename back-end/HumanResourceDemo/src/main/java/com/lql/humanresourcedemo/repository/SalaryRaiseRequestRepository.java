package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRaiseRequestRepository extends JpaRepository<SalaryRaiseRequest, Long> {
    Page<SalaryRaiseRequest> findAllByEmployeeId(Long employeeId, Pageable pageable);
}
