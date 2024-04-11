package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.attendance.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
}
