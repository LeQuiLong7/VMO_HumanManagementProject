package com.lql.humanresourcedemo.service.project;

import com.lql.humanresourcedemo.dto.request.admin.AssignEmployeeToProjectRequest;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewProjectRequest;
import com.lql.humanresourcedemo.dto.request.admin.UpdateProjectStatusRequest;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.dto.response.project.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.project.ProjectResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {
    Page<ProjectResponse> getAllProject(Pageable pageRequest);
    Page<ProjectDetail> getAllProjectsByEmployeeId(Long employeeId, Pageable pageRequest);
    List<EmployeeProjectResponse> getAllEmployeeInsideProject(Long projectId);

    ProjectResponse createNewProject(CreateNewProjectRequest request);

    ProjectResponse updateProject(UpdateProjectStatusRequest request);
    List<EmployeeProjectResponse> assignEmployeeToProject(AssignEmployeeToProjectRequest request);

}
