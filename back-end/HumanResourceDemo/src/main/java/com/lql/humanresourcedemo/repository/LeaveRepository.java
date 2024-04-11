package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface LeaveRepository extends JpaRepository<LeaveRequest, Long> {

    boolean existsByEmployeeAndDate(Employee e, LocalDate date);
}
