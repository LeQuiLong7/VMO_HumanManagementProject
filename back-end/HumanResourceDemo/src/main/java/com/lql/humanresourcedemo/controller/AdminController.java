package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public GetProfileResponse createNewEmployee(@RequestBody CreateNewEmployeeRequest createNewEmployeeRequest) {
        return adminService.createNewEmployee(createNewEmployeeRequest);
    }

    @PutMapping("/techStack")
    public TechStackResponse updateTechStackForEmployee(@RequestBody UpdateEmployeeTechStackRequest request) {
        return adminService.updateEmployeeTechStack(request);
    }

    @PutMapping("/salary")
    public SalaryRaiseResponse handleSalaryRaise(@RequestBody HandleSalaryRaiseRequest handleSalaryRaiseRequest) {
        return adminService.handleSalaryRaiseRequest(handleSalaryRaiseRequest);
    }

    @PostMapping("/project")
    public ProjectResponse createNewProject(@RequestBody CreateNewProjectRequest createNewProjectRequest) {
        return adminService.createNewProject(createNewProjectRequest);
    }
    @PutMapping("/project")
    public ProjectResponse updateProjectState(@RequestBody UpdateProjectStatusRequest updateProjectStatusRequest) {
        return adminService.updateProject(updateProjectStatusRequest);
    }

    @PutMapping("/project/assign")
    public AssignEmployeeToProjectRequest assignEmployeeToProject(@RequestBody AssignEmployeeToProjectRequest assignEmployeeToProjectRequest) {
        return adminService.assignEmployeeToProject(assignEmployeeToProjectRequest);
    }

}
