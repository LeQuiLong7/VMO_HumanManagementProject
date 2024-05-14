package com.lql.humanresourcedemo.repository.salary;

import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest_;
import org.springframework.data.jpa.domain.Specification;

public class SalaryRaiseSpecifications {
    public static Specification<SalaryRaiseRequest> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(SalaryRaiseRequest_.EMPLOYEE).get("id"), employeeId);
    }
    public static Specification<SalaryRaiseRequest> byStatus(SalaryRaiseRequestStatus status) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(SalaryRaiseRequest_.STATUS), status);
    }
    public static Specification<SalaryRaiseRequest> byId(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(SalaryRaiseRequest_.ID), id);
    }
}
