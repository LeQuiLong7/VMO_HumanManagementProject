package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
