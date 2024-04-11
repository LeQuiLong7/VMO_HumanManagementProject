package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.project.EmployeeProject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, EmployeeProject.EmployeeProjectId> {
}
