package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, EmployeeProject.EmployeeProjectId> {


    @Query(value = "select e.id.employee.id from EmployeeProject e where e.id.project.id = :projectId")
    List<Long> getAllEmployeesAssignedByProjectId(Long projectId);




//    @Query(value = "select e.id.employee from EmployeeProject e where e.id.project.id = :projectId")
    Page<EmployeeProject> findAllByIdProjectId(Long projectId, Pageable pageable);
    Page<EmployeeProject> findAllByIdEmployeeId(Long employeeId, Pageable pageable);
}


