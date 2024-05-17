package com.lql.humanresourcedemo.repository.leave;

import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest_;
import com.lql.humanresourcedemo.model.employee.Employee_;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class LeaveSpecifications {
    public static Specification<LeaveRequest> byId(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(LeaveRequest_.ID), id);
    }
    public static Specification<LeaveRequest> byPmId(Long pmId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(LeaveRequest_.EMPLOYEE).get(Employee_.MANAGED_BY).get(Employee_.ID), pmId);
    }
    public static Specification<LeaveRequest> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(LeaveRequest_.EMPLOYEE).get(Employee_.ID), employeeId);
    }
    public static Specification<LeaveRequest> byDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(LeaveRequest_.DATE), date);
    }

}
