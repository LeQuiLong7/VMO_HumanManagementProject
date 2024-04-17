package com.lql.humanresourcedemo.service.admin;

import com.lql.humanresourcedemo.dto.request.admin.CreateNewEmployeeRequest;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewProjectRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.newaccount.NewAccountException;
import com.lql.humanresourcedemo.model.client.Client;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.repository.*;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static com.lql.humanresourcedemo.enumeration.ProjectState.INITIATION;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private  EmployeeRepository employeeRepository;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private  MailService mailService;
    @Mock
    private  SalaryRaiseRequestRepository salaryRepository;
    @Mock
    private  EmployeeTechRepository employeeTechRepository;
    @Mock
    private  EmployeeProjectRepository employeeProjectRepository;
    @Mock
    private  TechRepository techRepository;
    @Mock
    private  ClientRepository clientRepository;
    @Mock
    private  ProjectRepository projectRepository;
    @Mock
    private  ValidateService validateService;
    @Mock
    private  ApplicationContext applicationContext;
    private  AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(employeeRepository, passwordEncoder, mailService, salaryRepository, employeeTechRepository, employeeProjectRepository, techRepository, clientRepository, projectRepository, validateService, applicationContext);
    }

    @Test
    void createNewEmployee_PersonalEmailAlreadyExists() {
        CreateNewEmployeeRequest request = new CreateNewEmployeeRequest("", "", null, "", "", 2D, Role.EMPLOYEE, 1L, null);
        when(employeeRepository.existsByPersonalEmail(anyString())).thenReturn(true);
        when(employeeRepository.existsByEmail(anyString())).thenReturn(false);

        assertThrows(
                NewAccountException.class,
                () -> adminService.createNewEmployee(request),
                "Personal email %s already exists".formatted(request.personalEmail())
        );
    }

    @Test
    void createNewEmployee_Success() {
        CreateNewEmployeeRequest request = new CreateNewEmployeeRequest("long", "le qui", null, "", "", 2D, Role.EMPLOYEE, 1L, null);
        when(employeeRepository.existsByPersonalEmail(anyString())).thenReturn(false);

        GetProfileResponse response = adminService.createNewEmployee(request);

        assertAll(
                () -> assertEquals("long", response.firstName()),
                () -> assertEquals("le qui", response.lastName()),
                () -> assertEquals("EMPLOYEE", response.role().toString())
        );

        verify(employeeRepository, times(1)).save(ArgumentMatchers.any(Employee.class));
        verify(mailService, times(1)).sendEmail(anyString(), anyString(), anyString());
        verify(passwordEncoder, times(1)).encode( anyString());
    }

    @Test
    void createNewProject() {
        CreateNewProjectRequest request = new CreateNewProjectRequest("p1", "", LocalDate.now(), LocalDate.now(), 1L);

        when(clientRepository.getReferenceById(anyLong())).thenReturn(new Client());
        when(projectRepository.save(ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse newProject = adminService.createNewProject(request);
        assertAll(
                () -> assertEquals(INITIATION, newProject.state()),
                () -> assertEquals("p1", newProject.name())
        );
        verify(projectRepository, times(1)).save(ArgumentMatchers.any(Project.class));

    }

    @Test
    void updateProject() {
    }

    @Test
    void assignEmployeeToProject() {
    }
}