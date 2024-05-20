package com.lql.humanresourcedemo.service.admin;

import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.project.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.project.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.model.tech.Tech;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    Page<GetProfileResponse> getAllEmployee(Pageable pageRequest);
    Page<GetProfileResponse> getAllPM(Pageable pageRequest);
    Page<ProjectResponse> getAllProject(Pageable pageRequest);
    Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Pageable pageRequest);
    Page<Tech> getAllTech(Pageable pageRequest);

    GetProfileResponse createNewEmployee(CreateNewEmployeeRequest request);
    ProjectResponse createNewProject(CreateNewProjectRequest request);

    ProjectResponse updateProject(UpdateProjectStatusRequest request);
    List<EmployeeProjectResponse> assignEmployeeToProject(AssignEmployeeToProjectRequest request);

    SalaryRaiseResponse handleSalaryRaiseRequest(Long adminId, HandleSalaryRaiseRequest handleRequest);
    TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request);

    TechStackResponse getTechStackByEmployeeId(Long empId);

    List<EmployeeProjectResponse> getAllEmployeeInsideProject(Long projectId);

}
