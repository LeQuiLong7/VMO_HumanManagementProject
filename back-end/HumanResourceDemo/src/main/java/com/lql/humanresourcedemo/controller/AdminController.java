package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.service.admin.AdminService;
import com.lql.humanresourcedemo.utility.ContextUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/employees")
    public Page<GetProfileResponse> getAllEmployee(@RequestParam(required = false, defaultValue = "0") String page,
                                                   @RequestParam(required = false, defaultValue = "10") String size,
                                                   @RequestParam(required = false) List<String> p,
                                                   @RequestParam(required = false) List<String> o) {
        return adminService.getAllEmployee(page, size, p, o);
    }

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
        return adminService.handleSalaryRaiseRequest(ContextUtility.getCurrentEmployeeId(), handleSalaryRaiseRequest);
    }
    @GetMapping("/project")
    public Page<Project> getAllProjects(@RequestParam(required = false, defaultValue = "0") String page,
                                        @RequestParam(required = false, defaultValue = "10") String size,
                                        @RequestParam(required = false) List<String> p,
                                        @RequestParam(required = false) List<String> o) {
        return adminService.getAllProject(page, size, p, o);
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
