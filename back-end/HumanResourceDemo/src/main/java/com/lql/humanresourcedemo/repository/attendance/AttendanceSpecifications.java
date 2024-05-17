package com.lql.humanresourcedemo.repository.attendance;

import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.Attendance_;
import com.lql.humanresourcedemo.model.employee.Employee_;
import org.springframework.data.jpa.domain.Specification;

public class AttendanceSpecifications {
    public static Specification<Attendance> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Attendance_.EMPLOYEE).get(Employee_.ID), employeeId);
    }

}
