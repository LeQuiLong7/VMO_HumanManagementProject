package com.lql.humanresourcedemo.repository.salary;

import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import org.springframework.data.jpa.domain.Specification;

public class SalaryRaiseSpecifications {
    public static Specification<SalaryRaiseRequest> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("employee").get("id"), employeeId);
    }
    public static Specification<SalaryRaiseRequest> byId(Long id) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), id);
    }
}
