package com.lql.humanresourcedemo.controller;


import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.service.admin.AdminService;
import com.lql.humanresourcedemo.utility.ContextUtility;
import com.lql.humanresourcedemo.utility.HelperUtility;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;
import static com.lql.humanresourcedemo.utility.HelperUtility.validateAndBuildPageRequest;

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

        return adminService.getAllEmployee(validateAndBuildPageRequest(page, size, p, o, Employee.class));
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
        return adminService.getAllProjectsByEmployeeId(employeeId, validateAndBuildPageRequest(page, size, p, o, Project.class));
    }

    @GetMapping("/pm")
    public Page<GetProfileResponse> getAllPM(@RequestParam(required = false, defaultValue = "0") String page,
                                             @RequestParam(required = false, defaultValue = "10") String size,
                                             @RequestParam(required = false, defaultValue = "id") List<String> p,
                                             @RequestParam(required = false, defaultValue = "asc") List<String> o) {
        return adminService.getAllPM(validateAndBuildPageRequest(page, size, p, o, Employee.class));
    }

    @GetMapping("/techStack")
    public Page<Tech> getAllTechStack(@RequestParam(required = false, defaultValue = "0") String page,
                                      @RequestParam(required = false, defaultValue = "10") String size,
                                      @RequestParam(required = false, defaultValue = "id") List<String> p,
                                      @RequestParam(required = false, defaultValue = "asc") List<String> o) {
        return adminService.getAllTech(validateAndBuildPageRequest(page, size, p, o, Tech.class));
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
        return adminService.getAllSalaryRaiseRequest(validateAndBuildPageRequest(page, size, p, o, SalaryRaiseRequest.class));
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
        return adminService.getAllProject(validateAndBuildPageRequest(page, size, p, o, Project.class));
    }

    @GetMapping("/project/{projectId}/employees")
    public Page<GetProfileResponse> getAllEmployeesInsideProjects(@RequestParam(required = false, defaultValue = "0") String page,
                                                                  @RequestParam(required = false, defaultValue = "10") String size,
                                                                  @RequestParam(required = false, defaultValue = "id") List<String> p,
                                                                  @RequestParam(required = false, defaultValue = "desc") List<String> o,
                                                                  @PathVariable Long projectId) {
        return adminService.getAllEmployeeInsideProject(projectId, validateAndBuildPageRequest(page, size, p, o, Employee.class));
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
