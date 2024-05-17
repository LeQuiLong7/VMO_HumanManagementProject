package com.lql.humanresourcedemo.repository.project;

import com.lql.humanresourcedemo.dto.response.project.AssignHistory;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeProjectRepository extends JpaRepository<EmployeeProject, EmployeeProject.EmployeeProjectId> , JpaSpecificationExecutor<EmployeeProject> {



//    @Query(value = "select e.id.employeeId from EmployeeProject e where e.id.projectId = :projectId")
//    List<Long> getAllEmployeesAssignedByProjectId(Long projectId);




    //    @Query(value = "select e.id.employee from EmployeeProject e where e.id.project.id = :projectId")
//    Page<EmployeeProject> findAllByIdProjectId(Long projectId, Pageable pageable);

//    @Query(value = "select new com.lql.humanresourcedemo.dto.response.AssignHistory(e.id, concat(e.lastName, ' ', e.firstName) , e.avatarUrl, e.role, ep.createdAt, ep.createdBy) from EmployeeProject ep inner join Employee e on ep.id.employeeId = e.id where ep.id.projectId = :projectId")
//    List<AssignHistory> getAssignHistoryByProjectId(Long projectId);

    //    @EntityGraph(attributePaths = {"id.project"})
//    Page<EmployeeProject> findAllByIdEmployeeId(Long employeeId, Pageable pageable);
}


