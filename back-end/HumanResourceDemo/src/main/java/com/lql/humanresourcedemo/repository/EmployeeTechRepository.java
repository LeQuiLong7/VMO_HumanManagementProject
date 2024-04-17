package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.dto.model.tech.EmployeeTechDTO;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeTechRepository extends JpaRepository<EmployeeTech, EmployeeTech.EmployeeTechId> {

    @Query(value = "select e.id.tech.id as techId, e.id.tech.name as techName, e.yearOfExperience as yearOfExperience from EmployeeTech e where e.id.employee.id = ?1")
    List<EmployeeTechDTO> findTechInfoByEmployeeId(Long id);

    @Modifying
    @Transactional
    @Query("update EmployeeTech  e set e.yearOfExperience = :yearOfExperience where e.id.employee.id = :employeeId and e.id.tech.id = :techId")
    int updateYearOfExperienceByEmployeeIdAndTechId(Long employeeId, Long techId, Double yearOfExperience);

    @Query("select case when count(e) > 0 then true else false end from EmployeeTech e where e.id.employee.id = :employeeId and e.id.tech.id = :techId")
    boolean existsByIdEmployeeIdAndIdTechId(Long employeeId, Long techId);


}