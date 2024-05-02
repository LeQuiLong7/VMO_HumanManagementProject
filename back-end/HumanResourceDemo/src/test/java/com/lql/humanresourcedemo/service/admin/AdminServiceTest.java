package com.lql.humanresourcedemo.service.admin;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.newaccount.NewAccountException;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.exception.model.tech.TechException;
import com.lql.humanresourcedemo.model.client.Client;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.repository.*;
import com.lql.humanresourcedemo.service.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.lql.humanresourcedemo.enumeration.ProjectState.*;
import static com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailService mailService;
    @Mock
    private SalaryRaiseRequestRepository salaryRepository;
    @Mock
    private EmployeeTechRepository employeeTechRepository;
    @Mock
    private EmployeeProjectRepository employeeProjectRepository;
    @Mock
    private TechRepository techRepository;

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private ApplicationContext applicationContext;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(employeeRepository, passwordEncoder, mailService, salaryRepository, employeeTechRepository, employeeProjectRepository, techRepository,  projectRepository, applicationContext);
    }

    @Test
    void createNewEmployee_PersonalEmailAlreadyExists() {
        CreateNewEmployeeRequest request = new CreateNewEmployeeRequest("", "", null, "", "", 2D, Role.EMPLOYEE, 1L, null);
        when(employeeRepository.existsByPersonalEmail(anyString())).thenReturn(true);

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
        verify(passwordEncoder, times(1)).encode(anyString());
    }

    @Test
    void createNewProject() {
        CreateNewProjectRequest request = new CreateNewProjectRequest("p1", "", LocalDate.now(), LocalDate.now());

        when(projectRepository.save(ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse newProject = adminService.createNewProject(request);
        assertAll(
                () -> assertEquals(INITIATION, newProject.state()),
                () -> assertEquals("p1", newProject.name())
        );
        verify(projectRepository, times(1)).save(ArgumentMatchers.any(Project.class));

    }

    @Test
    void updateProject_ProjectNotFound() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, INITIATION, null, null);

        when(projectRepository.findById(request.id())).thenReturn(Optional.empty());


        assertThrows(
                ProjectException.class,
                () -> adminService.updateProject(request),
                "Could not find project " + request.id()
        );
    }

    @Test
    void updateProject_ProjectAlreadyFinish() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, INITIATION, null, null);
        Project p = Project.builder()
                .state(FINISHED)
                .build();
        when(projectRepository.findById(request.id())).thenReturn(Optional.of(p));


        assertThrows(
                ProjectException.class,
                () -> adminService.updateProject(request),
                "Could not update project, project already finished "
        );
    }

    @Test
    void updateProject_NewStateEqualToOldState() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, ON_GOING, null, null);
        Project p = Project.builder()
                .state(ON_GOING)
                .build();
        when(projectRepository.findById(request.id())).thenReturn(Optional.of(p));

        assertThrows(
                ProjectException.class,
                () -> adminService.updateProject(request),
                "New state is not valid, project already in that state "
        );
    }


    @Test
    void updateProject_ActualStartDateIsRequired() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, ON_GOING, null, null);
        Project p = Project.builder()
                .state(INITIATION)
                .build();
        when(projectRepository.findById(request.id())).thenReturn(Optional.of(p));

        assertThrows(
                ProjectException.class,
                () -> adminService.updateProject(request),
                "Actual start date is required"
        );
    }

    @Test
    void updateProject_ActualFinishDateIsRequired() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, FINISHED, null, null);
        Project p = Project.builder()
                .state(ON_GOING)
                .build();
        when(projectRepository.findById(request.id())).thenReturn(Optional.of(p));

        assertThrows(
                ProjectException.class,
                () -> adminService.updateProject(request),
                "Actual finished date is required"
        );
    }

    @Test
    void updateProject_Success() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, FINISHED, LocalDate.now(), LocalDate.now());
        Project p = Project.builder()
                .state(ON_GOING)
                .client(new Client())
                .build();
        when(projectRepository.findById(request.id())).thenReturn(Optional.of(p));
        when(projectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        ProjectResponse response = adminService.updateProject(request);

        assertAll(
                () -> assertEquals(request.newState(), response.state())
        );

        verify(projectRepository, times(1)).save(any());
    }

    @Test
    void handleSalaryRaiseRequest_RaiseRequestNotFound() {

        HandleSalaryRaiseRequest request = new HandleSalaryRaiseRequest(1L, null, null);
        when(salaryRepository.findById(request.requestId())).thenReturn(Optional.empty());


        assertThrows(
                SalaryRaiseException.class,
                () -> adminService.handleSalaryRaiseRequest(1L, request),
                "Raise request doesn't exists"
        );
    }

    @Test
    void handleSalaryRaiseRequest_RaiseRequestAlreadyHandled() {

        HandleSalaryRaiseRequest handleRequest = new HandleSalaryRaiseRequest(1L, null, null);

        SalaryRaiseRequest raiseRequest = SalaryRaiseRequest.builder()
                .status(REJECTED)
                .build();

        when(salaryRepository.findById(handleRequest.requestId())).thenReturn(Optional.of(raiseRequest));


        assertThrows(
                SalaryRaiseException.class,
                () -> adminService.handleSalaryRaiseRequest(1L, handleRequest),
                "Raise handleRequest already handled"
        );
    }

    @Test
    void handleSalaryRaiseRequest_NewStatusNotValid() {

        HandleSalaryRaiseRequest handleRequest = new HandleSalaryRaiseRequest(1L, PROCESSING, null);

        SalaryRaiseRequest raiseRequest = SalaryRaiseRequest.builder()
                .status(PROCESSING)
                .build();

        when(salaryRepository.findById(handleRequest.requestId())).thenReturn(Optional.of(raiseRequest));


        assertThrows(
                SalaryRaiseException.class,
                () -> adminService.handleSalaryRaiseRequest(1L, handleRequest),
                "New status is not valid"
        );
    }

    @Test
    void handleSalaryRaiseRequest_Success() {

        HandleSalaryRaiseRequest handleRequest = new HandleSalaryRaiseRequest(1L, FULLY_ACCEPTED, null);

        SalaryRaiseRequest raiseRequest = SalaryRaiseRequest.builder()
                .employee(
                        Employee.builder()
                                .id(1L)
                                .build()
                )
                .expectedSalary(200D)
                .status(PROCESSING)
                .build();

        raiseRequest.setCreatedAt(LocalDateTime.now());

        when(salaryRepository.findById(handleRequest.requestId())).thenReturn(Optional.of(raiseRequest));

        SalaryRaiseResponse response = adminService.handleSalaryRaiseRequest(1L, handleRequest);


        assertAll(
                () -> assertEquals(handleRequest.status(), response.status()),
                () -> assertEquals(raiseRequest.getExpectedSalary(), response.newSalary())
        );
        verify(salaryRepository, times(1)).save(raiseRequest);
        verify(employeeRepository, times(1)).updateSalaryById(anyLong(), anyDouble());
//        verify(employeeRepository, times(1)).findById(anyLong(), eq(OnlyPersonalEmailAndFirstName.class));
        verify(mailService, times(1)).sendEmail(any(), any(), any());
    }


    @Test
    void updateEmployeeTechStack_EmployeeNotFound() {

        UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(1L, null);

        when(employeeRepository.existsById(request.employeeId())).thenReturn(false);


        assertThrows(
                EmployeeException.class,
                () -> adminService.updateEmployeeTechStack( request),
                "Could not find employee " + request.employeeId()
        );
    }

    @Test
    void updateEmployeeTechStack_TechNotFound() {

        TechStack techStack = new TechStack(1L, 2D);
        UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(1L, List.of(techStack));

        when(employeeRepository.existsById(request.employeeId())).thenReturn(true);
        when(techRepository.existsById(techStack.techId())).thenReturn(false);


        assertThrows(
                TechException.class,
                () -> adminService.updateEmployeeTechStack( request),
                "Tech id %s not found".formatted(techStack.techId())
        );
    }

    @Test
    void updateEmployeeTechStack_Success() {

        TechStack techStack = new TechStack(1L, 2D);
        UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(1L, List.of(techStack));

        when(employeeRepository.existsById(request.employeeId())).thenReturn(true);
        when(techRepository.existsById(techStack.techId())).thenReturn(true);
        when(employeeTechRepository.existsByIdEmployeeIdAndIdTechId(request.employeeId(), techStack.techId())).thenReturn(false);
        adminService.updateEmployeeTechStack(request);

        verify(employeeTechRepository, times(1)).save(any());
    }


    @Test
    void getAllEmployees() {


    }
}