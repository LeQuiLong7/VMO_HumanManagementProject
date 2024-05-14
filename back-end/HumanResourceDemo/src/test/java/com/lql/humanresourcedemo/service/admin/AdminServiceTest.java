package com.lql.humanresourcedemo.service.admin;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.exception.model.tech.TechException;
import com.lql.humanresourcedemo.model.client.Client;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectRepository;
import com.lql.humanresourcedemo.repository.project.ProjectRepository;
import com.lql.humanresourcedemo.repository.salary.SalaryRaiseRequestRepository;
import com.lql.humanresourcedemo.repository.tech.EmployeeTechRepository;
import com.lql.humanresourcedemo.repository.tech.TechRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.lql.humanresourcedemo.enumeration.ProjectState.*;
import static com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    Pageable pageable = Pageable.ofSize(10);

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
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        adminService = new AdminServiceImpl(employeeRepository, passwordEncoder, mailService, salaryRepository, employeeTechRepository, employeeProjectRepository, techRepository,  projectRepository);
    }

//    @Test
//    void createNewEmployee_PersonalEmailAlreadyExists() {
//        CreateNewEmployeeRequest request = new CreateNewEmployeeRequest("", "", null, "", "", 2D, Role.EMPLOYEE, 1L, null);
//        when(employeeRepository.existsByPersonalEmail(anyString())).thenReturn(true);
//
//        assertThrows(
//                NewAccountException.class,
//                () -> adminService.createNewEmployee(request),
//                "Personal email %s already exists".formatted(request.personalEmail())
//        );
//    }

    @Test
    void getAllEmployeeTest() {
        Employee employee = Employee.builder().id(1L).build();

        when(employeeRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(employee)));

        Page<GetProfileResponse> response = adminService.getAllEmployee(pageable);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(1L, response.getContent().get(0).id())
        );
    }

    @Test
    void getAllPMTest() {
        Employee employee = Employee.builder().id(1L).role(Role.PM).build();


        when(employeeRepository.findBy(any(Specification.class), any()))
                .thenReturn(new PageImpl<>(List.of(employee)));

        Page<GetProfileResponse> response = adminService.getAllPM(pageable);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(1L, response.getContent().get(0).id()),
                () -> assertEquals(Role.PM, response.getContent().get(0).role())
        );
    }

    @Test
    void getAllTech() {
        Tech tech = new Tech(1L, "a", null);


        when(techRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(tech)));

        Page<Tech> response = adminService.getAllTech(pageable);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(1L, response.getContent().get(0).getId()),
                () -> assertEquals("a", response.getContent().get(0).getName())
        );
    }

    @Test
    void getAllProject() {
        Project project = Project.builder()
                .id(1L)
                .name("a")
                .build();

        when(projectRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(project)));

        Page<ProjectResponse> response = adminService.getAllProject(pageable);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(1L, response.getContent().get(0).id()),
                () -> assertEquals("a", response.getContent().get(0).name())
        );
    }

    @Test
    void getAllSalaryRaiseRequest() {
        Employee employee = Employee.builder().id(1L).build();


        SalaryRaiseRequest salaryRaiseRequest = SalaryRaiseRequest.builder()
                .id(1L)
                .employee(employee)
                .status(PROCESSING)
                .build();

        when(salaryRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(salaryRaiseRequest)));

        Page<SalaryRaiseResponse> response = adminService.getAllSalaryRaiseRequest(pageable);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(1L, response.getContent().get(0).id()),
                () -> assertEquals(PROCESSING, response.getContent().get(0).status())
        );
    }

    @Test
    void getTechStackByEmployeeId() {

        Long employeeId = 1L;
        EmployeeTech et = new EmployeeTech(Mockito.mock(Employee.class), Mockito.mock(Tech.class), 1.2);

        when(employeeRepository.existsById(employeeId)).thenReturn(true);
//        when(employeeTechRepository.findTechInfoByEmployeeId(employeeId))
        when(employeeTechRepository.findBy(any(Specification.class), any()))
                .thenReturn(List.of(et));

        TechStackResponse response = adminService.getTechStackByEmployeeId(employeeId);
        assertAll(
                () -> assertEquals(employeeId, response.employeeId()),
                () -> assertEquals(1, response.techInfo().size())
        );
    }

    @Test
    void getTechStackByEmployeeId_EmployeeNotFound() {

        Long employeeId = 1L;

        when(employeeRepository.existsById(employeeId)).thenReturn(false);
        assertThrows(EmployeeException.class,
                () -> adminService.getTechStackByEmployeeId(employeeId));

    }
    @Test
    void getAllEmployeeInsideProject() {

        Employee employee = Employee.builder().id(1L).build();
        Project project = Project.builder().id(1L).build();

        EmployeeProject ep = new EmployeeProject(
                        employee,
                        project,
                10
        );
        when(projectRepository.existsById(any())).thenReturn(true);
        when(employeeProjectRepository.findBy(any(Specification.class), any()))
//        when(employeeProjectRepository.findAllByIdProjectId(project.getId(), pageable))
                .thenReturn(List.of(ep));

        List<EmployeeProjectResponse> response = adminService.getAllEmployeeInsideProject(project.getId());
        assertAll(
                () -> assertEquals(1, response.size())
//                () -> assertEquals(employee.getId(), response.getContent().get(0).id())
        );
    }

//    @Test
//    void getAllProjectsByEmployeeId() {
//
//        Employee employee = Employee.builder().id(1L).build();
//        Project project = Project.builder().id(1L).build();
//
//        EmployeeProject ep = new EmployeeProject(
//                        employee,
//                        project,
//                10
//        );
//        when(employeeRepository.existsById(employee.getId()))
//                .thenReturn(true);
//
//        when(employeeProjectRepository.findBy(any(Specification.class), any()))
//                .thenReturn(new PageImpl<>(List.of(ep)));
//
//        Page<ProjectDetail> response = adminService.getAllProjectsByEmployeeId(employee.getId(), pageable);
//        assertAll(
//                () -> assertEquals(1, response.getSize()),
//                () -> assertEquals(project.getId(), response.getContent().get(0).projectInfo().getId())
//        );
//    }


//    @Test
//    void assignEmployeeToProject() {
//
//        Employee employee = Employee.builder().id(1L).build();
//        Project project = Project.builder().id(1L).build();
//        AssignEmployeeToProjectRequest request = new AssignEmployeeToProjectRequest(project.getId(), List.of(
//                new AssignEmployeeToProjectRequest.EmployeeEffort(employee.getId(), 10)));
//
//        when(projectRepository.existsById(project.getId()))
//                .thenReturn(true);
//        when(employeeRepository.existsById(employee.getId()))
//                .thenReturn(true);
//        when(employeeProjectRepository.save(any(EmployeeProject.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        when(employeeProjectRepository.getAllEmployeesAssignedByProjectId(project.getId()))
//                .thenReturn(List.of(employee.getId()));
//
//        AssignEmployeeToProjectRequest response = adminService.assignEmployeeToProject(request);
//        assertAll(
//                () -> assertEquals(1, response.projectId())
////                () -> assertEquals(1, response.employeeIds().size())
//        );
//    }


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
    void createNewEmployee_Success() {
        CreateNewEmployeeRequest request = new CreateNewEmployeeRequest("long", "le qui", null, "", "", 2D, Role.EMPLOYEE, 1L, null);
//        when(employeeRepository.existsByPersonalEmail(anyString())).thenReturn(false);

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

        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.empty());


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
//        when(projectRepository.findById(request.id()))
        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(p));


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
        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(p));

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
//        when(projectRepository.findById(request.id()))
        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(p));

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
//        when(projectRepository.findById(request.id()))
        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(p));

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
                .employees(new ArrayList<>())
                .client(new Client())
                .build();
        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(p));
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
//        when(salaryRepository.findById(request.requestId()))
        when(salaryRepository.findBy(any(Specification.class), any()))

                .thenReturn(Optional.empty());


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

//        when(salaryRepository.findById(handleRequest.requestId()))
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(raiseRequest));


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
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(raiseRequest));


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

//        when(salaryRepository.findById(handleRequest.requestId()))
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(raiseRequest));

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
        when(employeeTechRepository.exists(any(Specification.class))).thenReturn(false);
        EmployeeTech ep = new EmployeeTech(Employee.builder().id(1L).build(), new Tech(1L, "a", null), 1D);
        when(employeeTechRepository.findBy(any(Specification.class), any())).thenReturn(
                List.of(ep)
        );

        adminService.updateEmployeeTechStack(request);
        verify(employeeTechRepository, times(1)).save(any());
    }
}