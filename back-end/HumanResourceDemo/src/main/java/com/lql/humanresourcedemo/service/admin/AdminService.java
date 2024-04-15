package com.lql.humanresourcedemo.service.admin;

import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;

public interface AdminService {
    GetProfileResponse createNewEmployee(CreateNewEmployeeRequest request);
    SalaryRaiseResponse handleSalaryRaiseRequest(HandleSalaryRaiseRequest handleRequest);

    TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request);
    ProjectResponse createNewProject(CreateNewProjectRequest request);

    ProjectResponse updateProject(UpdateProjectStatusRequest request);

    AssignEmployeeToProjectRequest assignEmployeeToProject(AssignEmployeeToProjectRequest request);
}
