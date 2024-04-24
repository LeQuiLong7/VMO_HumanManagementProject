package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);
    LeaveRequest findByEmployeeIdAndDate(Long employeeId, LocalDate date);
    Page<LeaveRequest> findAllByEmployeeId(Long employeeId, Pageable pageable);

//    @Query(value = "select l from LeaveRequest l join Employee  e on l.employee.id = e.id where e.managedBy.id = :pmId")
    Page<LeaveRequest> findAllByEmployeeManagedById(Long pmId, Pageable pageable);
}
