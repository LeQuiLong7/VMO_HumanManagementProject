package com.lql.humanresourcedemo.service.project;

import com.lql.humanresourcedemo.dto.request.admin.AssignEmployeeToProjectRequest;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewProjectRequest;
import com.lql.humanresourcedemo.dto.request.admin.UpdateProjectStatusRequest;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.dto.response.project.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.project.ProjectResponse;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.model.client.Client;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectRepository;
import com.lql.humanresourcedemo.repository.project.ProjectRepository;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.lql.humanresourcedemo.enumeration.ProjectState.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private EmployeeProjectRepository employeeProjectRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ValidateService validateService;

    private ProjectService projectService;

    @BeforeEach
    void setUp() {
        projectService = new ProjectServiceImpl(projectRepository, employeeProjectRepository, employeeRepository, validateService);
    }


    @Test
    void getAllProjectTest() {
        Project project = Project.builder()
                .id(1L)
                .name("a")
                .build();

        when(projectRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(project)));

        Page<ProjectResponse> response = projectService.getAllProject(Pageable.unpaged());
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(1L, response.getContent().get(0).id()),
                () -> assertEquals("a", response.getContent().get(0).name())
        );
        verify(projectRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllProjectsByEmployeeId() {

        Employee employee = Employee.builder().id(1L).build();
        Project project = Project.builder().id(1L).build();

        EmployeeProject ep = new EmployeeProject(
                employee,
                project,
                10
        );
        when(employeeRepository.existsById(employee.getId()))
                .thenReturn(true);

        when(employeeProjectRepository.findBy(any(Specification.class), any()))
                .thenReturn(new PageImpl<>(List.of(ep)));

        Page<ProjectDetail> response = projectService.getAllProjectsByEmployeeId(employee.getId(), Pageable.unpaged());
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(project.getId(), response.getContent().get(0).projectInfo().getId())
        );
        verify(employeeRepository, times(1)).existsById(employee.getId());
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());

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
                .thenReturn(List.of(ep));

        List<EmployeeProjectResponse> response = projectService.getAllEmployeeInsideProject(project.getId());
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals(employee.getId(), response.get(0).employeeId())
        );
        verify(projectRepository, times(1)).existsById(project.getId());
        verify(employeeProjectRepository, times(1)).findBy(any(Specification.class), any());

    }

    @Test
    void createNewProject() {
        CreateNewProjectRequest request = new CreateNewProjectRequest("p1", "", LocalDate.now(), LocalDate.now());

        when(projectRepository.save(ArgumentMatchers.any())).thenAnswer(invocation -> invocation.getArgument(0));

        ProjectResponse newProject = projectService.createNewProject(request);
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
                () -> projectService.updateProject(request),
                "Could not find project " + request.id()
        );
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());
    }

    @Test
    void updateProject_ProjectAlreadyFinish() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, INITIATION, null, null);
        Project p = Project.builder()
                .state(FINISHED)
                .build();
        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(p));


        assertThrows(
                ProjectException.class,
                () -> projectService.updateProject(request),
                "Could not update project, project already finished "
        );
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());

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
                () -> projectService.updateProject(request),
                "New state is not valid, project already in that state "
        );
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());

    }


    @Test
    void updateProject_ActualStartDateIsRequired() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, ON_GOING, null, null);
        Project p = Project.builder()
                .state(INITIATION)
                .build();
        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(p));

        assertThrows(
                ProjectException.class,
                () -> projectService.updateProject(request),
                "Actual start date is required"
        );
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());

    }

    @Test
    void updateProject_ActualFinishDateIsRequired() {

        UpdateProjectStatusRequest request = new UpdateProjectStatusRequest(1L, FINISHED, null, null);
        Project p = Project.builder()
                .state(ON_GOING)
                .build();
        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(p));

        assertThrows(
                ProjectException.class,
                () -> projectService.updateProject(request),
                "Actual finished date is required"
        );
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());

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
        ProjectResponse response = projectService.updateProject(request);

        assertAll(
                () -> assertEquals(request.newState(), response.state())
        );
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());
        verify(projectRepository, times(1)).save(any());
    }


    @Test
    void assignEmployeeToProject_EmployeeAlreadyAssigned() {

        Employee employee = Employee.builder().id(1L).build();
        Project project = Project.builder().id(1L)
                .employees((List.of(new EmployeeProject(employee, null, 10))))
                .build();

        AssignEmployeeToProjectRequest request = new AssignEmployeeToProjectRequest(project.getId(), List.of(
                new AssignEmployeeToProjectRequest.EmployeeEffort(employee.getId(), 10)));

        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(List.of(employee.getId()));

        List<EmployeeProjectResponse> response = projectService.assignEmployeeToProject(request);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals(employee.getId(), response.get(0).employeeId())
        );
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());
    }

    @Test
    void assignEmployeeToProject() {

        Employee employee = Employee.builder().id(1L).build();
        Project project = Project.builder().id(1L)
                .employees((List.of()))
                .build();

        AssignEmployeeToProjectRequest request = new AssignEmployeeToProjectRequest(project.getId(), List.of(
                new AssignEmployeeToProjectRequest.EmployeeEffort(employee.getId(), 10)));

        when(projectRepository.findBy(any(Specification.class), any()))
                .thenReturn(List.of(employee.getId()));
        when(employeeRepository.exists(any(Specification.class))).thenReturn(true);
        when(employeeProjectRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<EmployeeProjectResponse> response = projectService.assignEmployeeToProject(request);
        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals(employee.getId(), response.get(0).employeeId())
        );
        verify(projectRepository, times(1)).findBy(any(Specification.class), any());
        verify(employeeRepository, times(1)).exists(any(Specification.class));
        verify(employeeRepository, times(1)).updateCurrentEffortById(employee.getId(), 10);

    }

}