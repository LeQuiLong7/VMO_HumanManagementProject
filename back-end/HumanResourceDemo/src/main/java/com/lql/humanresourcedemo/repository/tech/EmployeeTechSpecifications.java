package com.lql.humanresourcedemo.repository.tech;

import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeTechSpecifications {

    public static Specification<EmployeeTech> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id").get("employeeId"), employeeId);
    }
}
