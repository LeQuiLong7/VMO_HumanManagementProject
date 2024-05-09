package com.lql.humanresourcedemo.repository.project;

import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {
    boolean existsById(Long id);

}
