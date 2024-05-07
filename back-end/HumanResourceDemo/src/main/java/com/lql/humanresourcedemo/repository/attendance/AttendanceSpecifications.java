package com.lql.humanresourcedemo.repository.attendance;

import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class AttendanceSpecifications {
    public static Specification<Attendance> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("employee").get("id"), employeeId);
    }

}
