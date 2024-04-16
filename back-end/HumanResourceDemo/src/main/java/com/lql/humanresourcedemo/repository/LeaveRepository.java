package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);
    LeaveRequest findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    Page<LeaveRequest> findAllByEmployeeId(Long employeeId, Pageable pageable);
}
