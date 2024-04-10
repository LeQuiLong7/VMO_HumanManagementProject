package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalaryRaiseRequestRepository extends JpaRepository<SalaryRaiseRequest, Long> {
}
