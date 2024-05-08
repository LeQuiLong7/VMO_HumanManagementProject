package com.lql.humanresourcedemo.service.admin;

import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.tech.Tech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    Page<GetProfileResponse> getAllEmployee(Pageable pageRequest);
    Page<GetProfileResponse> getAllPM(Pageable pageRequest);
    Page<ProjectResponse> getAllProject(Pageable pageRequest);
    GetProfileResponse createNewEmployee(CreateNewEmployeeRequest request);
    SalaryRaiseResponse handleSalaryRaiseRequest(Long adminId, HandleSalaryRaiseRequest handleRequest) throws SalaryRaiseException;

    TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request);
    ProjectResponse createNewProject(CreateNewProjectRequest request);

    ProjectResponse updateProject(UpdateProjectStatusRequest request);

    List<EmployeeProjectResponse> assignEmployeeToProject(AssignEmployeeToProjectRequest request);
//    AssignEmployeeToProjectRequest assignEmployeeToProject(AssignEmployeeToProjectRequest request);

    Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Pageable pageRequest);

    Page<Tech> getAllTech(Pageable pageRequest);

    TechStackResponse getTechStackByEmployeeId(Long empId) throws EmployeeException;

    List<EmployeeProjectResponse> getAllEmployeeInsideProject(Long projectId);

    Page<ProjectDetail> getAllProjectsByEmployeeId(Long employeeId, Pageable pageRequest);
}
