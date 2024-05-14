package com.lql.humanresourcedemo.repository.project;

import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.EmployeeProject_;
import org.springframework.data.jpa.domain.Specification;

public class EmployeeProjectSpecifications {

    public static Specification<EmployeeProject> byProjectId(Long projectId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmployeeProject_.ID).get("projectId"), projectId);
    }
    public static Specification<EmployeeProject> byEmployeeId(Long employeeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(EmployeeProject_.ID).get("employeeId"), employeeId);
    }
}
