package com.lql.humanresourcedemo.service.admin;

import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.service.project.ProjectService;
import com.lql.humanresourcedemo.service.salary.SalaryService;
import com.lql.humanresourcedemo.service.search.SearchService;
import com.lql.humanresourcedemo.service.tech.TechService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static com.lql.humanresourcedemo.enumeration.ProjectState.INITIATION;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class AdminServiceImplTest {

    Pageable pageable = Pageable.ofSize(10);
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailService mailService;
    @Mock
    private SalaryService salaryService;
    @Mock
    private SearchService searchService;
    @Mock
    private ProjectService projectService;
    @Mock
    private TechService techService;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(employeeRepository, passwordEncoder, mailService, salaryService, searchService, projectService, techService);
    }

    @Test
    void getAllEmployeeTest() {

        when(searchService.search(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        adminService.getAllEmployee(pageable);

        verify(searchService, times(1)).search(any(Specification.class), eq(pageable));
    }

    @Test
    void getAllPMTest() {
        when(searchService.search(any(Specification.class), eq(pageable))).thenReturn(Page.empty());

        adminService.getAllPM(pageable);

        verify(searchService, times(1)).search(any(Specification.class), any());
    }

    @Test
    void getAllTech() {

        adminService.getAllTech(pageable);
        verify(techService, times(1)).getAllTech(pageable);
    }

    @Test
    void getAllProject() {

        adminService.getAllProject(pageable);
        verify(projectService, times(1)).getAllProject(pageable);
    }

    @Test
    void getAllSalaryRaiseRequest() {
        adminService.getAllSalaryRaiseRequest(pageable);

        verify(salaryService, times(1)).getAllSalaryRaiseRequest(null, Role.ADMIN, pageable);
    }

    @Test
    void getTechStackByEmployeeId() {

        Long employeeId = 1L;
        adminService.getTechStackByEmployeeId(employeeId);

        verify(techService, times(1)).getTechStackByEmployeeId(employeeId);
    }

    @Test
    void getAllEmployeesInsideProject() {

        long projectId = 1L;
        adminService.getAllEmployeeInsideProject(projectId);

        verify(projectService, times(1)).getAllEmployeeInsideProject(projectId);
    }


    @Test
    void createNewEmployee_EmailAlreadyExists() {
        CreateNewEmployeeRequest request = new CreateNewEmployeeRequest("long", "le qui", null, "", "", 2D, Role.EMPLOYEE, 1L, null);
        when(employeeRepository.existsByEmail(anyString())).thenReturn(true);
        when(employeeRepository.countByEmailLike(anyString())).thenReturn(2);

        GetProfileResponse response = adminService.createNewEmployee(request);

        assertAll(
                () -> assertEquals("long", response.firstName()),
                () -> assertEquals("le qui", response.lastName()),
                () -> assertEquals("longlq2@company.com", response.email()),
                () -> assertEquals("EMPLOYEE", response.role().toString())
        );

        verify(employeeRepository, times(1)).save(ArgumentMatchers.any(Employee.class));
        verify(mailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
    }


    @Test
    void createNewEmployee_EmailDoesNotExists() {
        CreateNewEmployeeRequest request = new CreateNewEmployeeRequest("long", "le qui", null, "", "", 2D, Role.EMPLOYEE, 1L, null);
        GetProfileResponse response = adminService.createNewEmployee(request);


        assertAll(
                () -> assertEquals("long", response.firstName()),
                () -> assertEquals("longlq@company.com", response.email()),
                () -> assertEquals("le qui", response.lastName()),
                () -> assertEquals("EMPLOYEE", response.role().toString())
        );

        verify(employeeRepository, times(1)).save(ArgumentMatchers.any(Employee.class));
        verify(mailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void createNewProject() {
        CreateNewProjectRequest request = new CreateNewProjectRequest("p1", "", LocalDate.now(), LocalDate.now());
        adminService.createNewProject(request);

        verify(projectService, times(1)).createNewProject(request);

    }

    @Test
    void updateProject() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, INITIATION, null, null);

        adminService.updateProject(request);

        verify(projectService, times(1)).updateProject(request);
    }
    @Test
    void assignEmployeeToProject() {

        AssignEmployeeToProjectRequest request = new AssignEmployeeToProjectRequest(null, null);
        adminService.assignEmployeeToProject(request);

        verify(projectService, times(1)).assignEmployeeToProject(request);
    }

    @Test
    void handleSalaryRaiseRequest() {
        Long adminId = 1L;
        HandleSalaryRaiseRequest request = new HandleSalaryRaiseRequest(1L, null, null);
        adminService.handleSalaryRaiseRequest(adminId, request);

        verify(salaryService, times(1)).handleSalaryRaiseRequest(adminId, request);
    }

    @Test
    void updateEmployeeTechStack() {

        UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(1L, null);

        adminService.updateEmployeeTechStack(request);
        verify(techService, times(1)).updateEmployeeTechStack(request);
    }
}