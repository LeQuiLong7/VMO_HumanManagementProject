package com.lql.humanresourcedemo.repository.passwordreset;

import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.password.PasswordResetRequest;
import com.lql.humanresourcedemo.model.password.PasswordResetRequest_;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PasswordResetSpecifications {
    public static Specification<PasswordResetRequest> byToken(String token) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PasswordResetRequest_.ID).get("token"), token);
    }
    public static Specification<PasswordResetRequest> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(PasswordResetRequest_.EMPLOYEE).get("employeeId"), employeeId);
    }

}
