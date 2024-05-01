package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.service.admin.AdminService;
import com.lql.humanresourcedemo.utility.ContextUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;


    @GetMapping("/employees")
    public Page<GetProfileResponse> getAllEmployee(@RequestParam(required = false, defaultValue = "0") String page,
                                                   @RequestParam(required = false, defaultValue = "10") String size,
                                                   @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                   @RequestParam(required = false, defaultValue = "asc") List<String> o) {
        return adminService.getAllEmployee(page, size, p, o);
    }

    @PostMapping("/employees")
    public GetProfileResponse createNewEmployee(@RequestBody @Valid CreateNewEmployeeRequest createNewEmployeeRequest) {
        return adminService.createNewEmployee(createNewEmployeeRequest);
    }

    @GetMapping("/employee/{employeeId}/projects")
    public Page<ProjectDetail> getAllProjectsByEmployeeId(@RequestParam(required = false, defaultValue = "0") String page,
                                                          @RequestParam(required = false, defaultValue = "10") String size,
                                                          @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                          @RequestParam(required = false, defaultValue = "asc") List<String> o,
                                                          @PathVariable Long employeeId) {
        return adminService.getAllProjectsByEmployeeId(employeeId, page, size, p, o);
    }

    @GetMapping("/pm")
    public Page<GetProfileResponse> getAllPM(@RequestParam(required = false, defaultValue = "0") String page,
                                             @RequestParam(required = false, defaultValue = "10") String size,
                                             @RequestParam(required = false, defaultValue = "id") List<String> p,
                                             @RequestParam(required = false, defaultValue = "asc") List<String> o) {
        return adminService.getAllPM(page, size, p, o);
    }

    @GetMapping("/techStack")
    public Page<Tech> getAllTechStack(@RequestParam(required = false, defaultValue = "0") String page,
                                      @RequestParam(required = false, defaultValue = "10") String size,
                                      @RequestParam(required = false, defaultValue = "id") List<String> p,
                                      @RequestParam(required = false, defaultValue = "asc") List<String> o) {
        return adminService.getAllTech(page, size, p, o);
    }

    @GetMapping("/techStack/{empId}")
    public TechStackResponse getTechStack(@PathVariable Long empId) {
        return adminService.getTechStackByEmployeeId(empId);
    }



    @PutMapping("/techStack")
    public TechStackResponse updateTechStackForEmployee(@RequestBody UpdateEmployeeTechStackRequest request) {
        return adminService.updateEmployeeTechStack(request);
    }


    @GetMapping("/salary")
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(@RequestParam(required = false, defaultValue = "0") String page,
                                                              @RequestParam(required = false, defaultValue = "10") String size,
                                                              @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                              @RequestParam(required = false, defaultValue = "asc") List<String> o) {
        return adminService.getAllSalaryRaiseRequest(page, size, p, o);
    }

    @PutMapping("/salary")
    public SalaryRaiseResponse handleSalaryRaise(@RequestBody HandleSalaryRaiseRequest handleSalaryRaiseRequest) {
        return adminService.handleSalaryRaiseRequest(ContextUtility.getCurrentEmployeeId(), handleSalaryRaiseRequest);
    }

    @GetMapping("/project")
    public Page<ProjectResponse> getAllProjects(@RequestParam(required = false, defaultValue = "0") String page,
                                                @RequestParam(required = false, defaultValue = "10") String size,
                                                @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                @RequestParam(required = false, defaultValue = "desc") List<String> o) {
        return adminService.getAllProject(page, size, p, o);
    }

    @GetMapping("/project/{projectId}/employees")
    public Page<GetProfileResponse> getAllEmployeesInsideProjects(@RequestParam(required = false, defaultValue = "0") String page,
                                                                  @RequestParam(required = false, defaultValue = "10") String size,
                                                                  @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                                  @RequestParam(required = false, defaultValue = "desc") List<String> o,
                                                                  @PathVariable Long projectId) {
        return adminService.getAllEmployeeInsideProject(projectId, page, size, p, o);
    }

    @PostMapping("/project")
    public ProjectResponse createNewProject(@RequestBody @Valid CreateNewProjectRequest createNewProjectRequest) {
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
