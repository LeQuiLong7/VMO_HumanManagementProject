package com.lql.humanresourcedemo.repository.project;

import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.project.Project_;
import org.springframework.data.jpa.domain.Specification;

public class ProjectSpecifications {
    public static Specification<Project> byProjectId(Long projectId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get(Project_.ID), projectId);
    }
}
