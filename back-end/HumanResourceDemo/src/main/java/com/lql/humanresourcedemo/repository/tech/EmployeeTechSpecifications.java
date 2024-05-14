package com.lql.humanresourcedemo.repository.tech;

import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeTechSpecifications {

    public static Specification<EmployeeTech> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id").get("employeeId"), employeeId);
    }
    public static Specification<EmployeeTech> byTechId(Long techId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id").get("techId"), techId);
    }
}
