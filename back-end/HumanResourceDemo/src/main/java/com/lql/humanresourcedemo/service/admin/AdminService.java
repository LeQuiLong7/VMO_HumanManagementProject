package com.lql.humanresourcedemo.service.admin;

import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.model.project.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminService {

    Page<GetProfileResponse> getAllEmployee(String page, String pageSize, List<String> properties, List<String> orders);
    Page<Project> getAllProject(String page, String pageSize, List<String> properties, List<String> orders);
    GetProfileResponse createNewEmployee(CreateNewEmployeeRequest request);
    SalaryRaiseResponse handleSalaryRaiseRequest(Long adminId, HandleSalaryRaiseRequest handleRequest);

    TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request);
    ProjectResponse createNewProject(CreateNewProjectRequest request);

    ProjectResponse updateProject(UpdateProjectStatusRequest request);

    AssignEmployeeToProjectRequest assignEmployeeToProject(AssignEmployeeToProjectRequest request);
}