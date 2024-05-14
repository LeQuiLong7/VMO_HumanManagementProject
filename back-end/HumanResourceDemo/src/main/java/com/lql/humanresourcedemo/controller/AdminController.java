package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.service.admin.AdminService;
import com.lql.humanresourcedemo.utility.ContextUtility;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "6. Admin controller")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/employees")
    public Page<GetProfileResponse> getAllEmployee(Pageable page) {

        return adminService.getAllEmployee(page);
    }

    @PostMapping("/employees")
    @ResponseStatus(HttpStatus.CREATED)
    public GetProfileResponse createNewEmployee(@RequestBody @Valid CreateNewEmployeeRequest createNewEmployeeRequest) {
        return adminService.createNewEmployee(createNewEmployeeRequest);
    }

    @GetMapping("/employee/{employeeId}/projects")
    public Page<ProjectDetail> getAllProjectsByEmployeeId(Pageable page, @PathVariable Long employeeId) {
        return adminService.getAllProjectsByEmployeeId(employeeId, page);
    }

    @GetMapping("/pm")
    public Page<GetProfileResponse> getAllPM(Pageable page) {
        return adminService.getAllPM(page);
    }

    @GetMapping("/techStack")
    public Page<Tech> getAllTechStack(Pageable page) {
        return adminService.getAllTech(page);
    }

    @GetMapping("/techStack/{empId}")
    public TechStackResponse getTechStackByEmployeeId(@PathVariable Long empId) {
        return adminService.getTechStackByEmployeeId(empId);
    }


    @PutMapping("/techStack")
    public TechStackResponse updateTechStackForEmployee(@RequestBody @Valid UpdateEmployeeTechStackRequest request) {
        return adminService.updateEmployeeTechStack(request);
    }


    @GetMapping("/salary")
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Pageable page) {
        return adminService.getAllSalaryRaiseRequest(page);
    }

    @PutMapping("/salary")
    public SalaryRaiseResponse handleSalaryRaise(@RequestBody @Valid HandleSalaryRaiseRequest handleSalaryRaiseRequest) {
        return adminService.handleSalaryRaiseRequest(ContextUtility.getCurrentEmployeeId(), handleSalaryRaiseRequest);
    }

    @GetMapping("/project")
    public Page<ProjectResponse> getAllProjects(Pageable page) {
        return adminService.getAllProject(page);
    }

    @GetMapping("/project/{projectId}/employees")
    public List<EmployeeProjectResponse> getAllEmployeesInsideProjects(@PathVariable Long projectId) {
        return adminService.getAllEmployeeInsideProject(projectId);
    }

    @PostMapping("/project")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectResponse createNewProject(@RequestBody @Valid CreateNewProjectRequest createNewProjectRequest) {
        return adminService.createNewProject(createNewProjectRequest);
    }

    @PutMapping("/project")
    public ProjectResponse updateProjectState(@RequestBody @Valid UpdateProjectStatusRequest updateProjectStatusRequest) {
        return adminService.updateProject(updateProjectStatusRequest);
    }

    @PutMapping("/project/assign")
    public List<EmployeeProjectResponse> assignEmployeeToProject(@RequestBody @Valid AssignEmployeeToProjectRequest assignEmployeeToProjectRequest) {
        return adminService.assignEmployeeToProject(assignEmployeeToProjectRequest);
    }

}
