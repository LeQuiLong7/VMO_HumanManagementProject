package com.lql.humanresourcedemo.repository.project;

import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.Project;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {
    public static Specification<Project> byProjectId(Long projectId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id"), projectId);
    }
}
