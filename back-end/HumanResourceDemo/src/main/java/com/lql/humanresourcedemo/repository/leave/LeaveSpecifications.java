package com.lql.humanresourcedemo.repository.leave;

import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class LeaveSpecifications {
    public static Specification<LeaveRequest> byPmId(Long pmId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("employee").get("managedBy").get("id"), pmId);
    }
    public static Specification<LeaveRequest> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("employee").get("id"), employeeId);
    }

    public static Specification<LeaveRequest> byDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), date);
    }

}
