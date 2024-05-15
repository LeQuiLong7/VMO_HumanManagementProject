package com.lql.humanresourcedemo.repository.tech;

import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface EmployeeTechRepository extends JpaRepository<EmployeeTech, EmployeeTech.EmployeeTechId>, JpaSpecificationExecutor<EmployeeTech> {

    @Modifying
    @Transactional
    @Query("update EmployeeTech  e set e.yearOfExperience = :yearOfExperience where e.id.employeeId = :employeeId and e.id.techId = :techId")
    void updateYearOfExperienceByEmployeeIdAndTechId(Long employeeId, Long techId, Double yearOfExperience);

}