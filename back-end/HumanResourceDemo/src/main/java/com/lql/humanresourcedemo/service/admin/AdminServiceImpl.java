package com.lql.humanresourcedemo.service.admin;


import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.project.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.service.project.ProjectService;
import com.lql.humanresourcedemo.service.salary.SalaryService;
import com.lql.humanresourcedemo.service.search.SearchService;
import com.lql.humanresourcedemo.service.tech.TechService;
import com.lql.humanresourcedemo.util.MappingUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications.all;
import static com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications.byRole;
import static com.lql.humanresourcedemo.util.HelperUtil.*;
import static com.lql.humanresourcedemo.util.MappingUtil.employeeToProfileResponse;
import static com.lql.humanresourcedemo.util.MappingUtil.toEmployee;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SalaryService salaryService;
    private final SearchService searchService;
    private final ProjectService projectService;
    private final TechService techService;

    @Override
    public Page<GetProfileResponse> getAllEmployee(Pageable pageRequest) {
        return searchService.search(all(), pageRequest).map(MappingUtil::employeeToProfileResponse);

    }

    @Override
    public Page<GetProfileResponse> getAllPM(Pageable pageRequest) {
        return searchService.search(byRole(Role.PM), pageRequest).map(MappingUtil::employeeToProfileResponse);
    }

    @Override
    public Page<ProjectResponse> getAllProject(Pageable pageRequest) {
        return projectService.getAllProject(pageRequest);
    }

    @Override
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Pageable pageRequest) {
        return salaryService.getAllSalaryRaiseRequest(null, Role.ADMIN, pageRequest);
    }

    @Override
    public Page<Tech> getAllTech(Pageable pageRequest) {
        return techService.getAllTech(pageRequest);
    }

    @Override
    public TechStackResponse getTechStackByEmployeeId(Long empId){
        return techService.getTechStackByEmployeeId(empId);
    }


    @Override
    public List<EmployeeProjectResponse> getAllEmployeeInsideProject(Long projectId) {
        return projectService.getAllEmployeeInsideProject(projectId);
    }

    @Override
    @Transactional
    public GetProfileResponse createNewEmployee(CreateNewEmployeeRequest request) {
        // create email address base on the employee name : Le Qui Long -> longlq@company.com
        String email = buildEmail(request.firstName(), request.lastName());
        // add a number identifier if the email already exists: longlq2@company.com
        if (employeeRepository.existsByEmail(email)) {
            int count = employeeRepository.countByEmailLike(buildEmailWithWildcard(email));
            email = emailWithIdentityNumber(email, count);
        }
        // random UUID password
        String password = UUID.randomUUID().toString();

        Employee e = toEmployee(request, employeeRepository.getReferenceById(request.managedBy()), email, passwordEncoder.encode(password));

        employeeRepository.save(e);

        // send email address and password to employee's personal email
        mailService.sendEmail(e.getPersonalEmail(), "[COMPANY] - WELCOME NEW EMPLOYEE", buildWelcomeMailMessage(e.getFirstName() + " " + e.getLastName(), e.getEmail(), password));
        // map to response
        return employeeToProfileResponse(e);
    }

    @Override
    public ProjectResponse createNewProject(CreateNewProjectRequest request) {
        return projectService.createNewProject(request);
    }

    @Override
    public SalaryRaiseResponse handleSalaryRaiseRequest(Long adminId, HandleSalaryRaiseRequest handleRequest)  {
        return salaryService.handleSalaryRaiseRequest(adminId, handleRequest);
    }

    @Override
    public TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request) {
        return techService.updateEmployeeTechStack(request);
    }


    @Override
    public ProjectResponse updateProject(UpdateProjectStatusRequest request) {
        return projectService.updateProject(request);
    }

    @Override
    public List<EmployeeProjectResponse> assignEmployeeToProject(AssignEmployeeToProjectRequest request) {
        return projectService.assignEmployeeToProject(request);
    }



//    private void validateCreateNewEmployeeRequest(CreateNewEmployeeRequest request) {
//        if (employeeRepository.existsByPersonalEmail(request.personalEmail())) {
//            throw new NewAccountException("Personal email %s already exists".formatted(request.personalEmail()));
//        }
//    }
}
