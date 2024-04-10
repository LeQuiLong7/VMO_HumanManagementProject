package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.dto.model.EmployeeTechDTO;
import com.lql.humanresourcedemo.dto.model.TechStack;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeTechRepository extends JpaRepository<EmployeeTech, EmployeeTech.EmployeeTechId> {

    @Query(value = "select e.id.tech.id as techId, e.id.tech.name as techName, e.yearOfExperience as yearOfExperience from EmployeeTech e where e.id.employee.id = ?1")
    List<EmployeeTechDTO> findTechInfoByEmployeeId(Long id);


    List<EmployeeTech> findByIdEmployeeId(Long employeeId);


//    @Query(value = "select e.id.tech.id as techId, e.id.tech.name as techName, e.yearOfExperience as yearOfExperience from EmployeeTech e where e.id.employee.id = ?1")
//    List<EmployeeTechDTO> findTechInfoByEmployeeId(Long id);
}