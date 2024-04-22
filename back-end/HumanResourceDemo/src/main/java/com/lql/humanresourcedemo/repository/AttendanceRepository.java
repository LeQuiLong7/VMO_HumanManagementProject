package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.attendance.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDate(LocalDate date);
    List<Attendance> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate startDate, LocalDate endDate);

    Page<Attendance> findAllByEmployeeId(Long employeeId, Pageable pageable);
    boolean existsByEmployeeIdAndDate(Long employeeId, LocalDate date);
}
