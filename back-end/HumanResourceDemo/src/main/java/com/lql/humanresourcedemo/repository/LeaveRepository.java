package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);
    LeaveRequest findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    Page<LeaveRequest> findAllByEmployeeId(Long employeeId, Pageable pageable);

    @Query(value = "select l from LeaveRequest l where l.employee.managedBy.id = :pmId")
    Page<LeaveRequest> findAllByPMId(Long pmId, Pageable pageable);
}
