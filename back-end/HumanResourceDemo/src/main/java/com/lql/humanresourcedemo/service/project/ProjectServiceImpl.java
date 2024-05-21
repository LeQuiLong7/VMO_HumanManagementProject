package com.lql.humanresourcedemo.service.project;


import com.lql.humanresourcedemo.dto.request.admin.AssignEmployeeToProjectRequest;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewProjectRequest;
import com.lql.humanresourcedemo.dto.request.admin.UpdateProjectStatusRequest;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.dto.response.project.AssignHistory;
import com.lql.humanresourcedemo.dto.response.project.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.project.ProjectResponse;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.EmployeeProject_;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.project.Project_;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectRepository;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectSpecifications;
import com.lql.humanresourcedemo.repository.project.ProjectRepository;
import com.lql.humanresourcedemo.repository.project.ProjectSpecifications;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.util.MappingUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.lql.humanresourcedemo.enumeration.ProjectState.*;
import static com.lql.humanresourcedemo.enumeration.ProjectState.FINISHED;
import static com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications.byCurrentEffortLessThanOrEqualTo;
import static com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications.byId;
import static com.lql.humanresourcedemo.repository.project.EmployeeProjectSpecifications.byProjectId;
import static com.lql.humanresourcedemo.util.MappingUtil.projectToProjectResponse;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService{
    private final ProjectRepository projectRepository;
    private final EmployeeProjectRepository employeeProjectRepository;
    private final EmployeeRepository employeeRepository;
    private final ValidateService validateService;

    @Override
    public Page<ProjectResponse> getAllProject(Pageable pageRequest) {
        return projectRepository.findAll(pageRequest).map(MappingUtil::projectToProjectResponse);
    }

    @Override
    public Page<ProjectDetail> getAllProjectsByEmployeeId(Long employeeId, Pageable pageRequest) {
        validateService.requireExistsEmployee(employeeId);

        Page<EmployeeProject> projects = employeeProjectRepository.findBy(EmployeeProjectSpecifications.byEmployeeId(employeeId), p -> p.project(EmployeeProject_.PROJECT).sortBy(pageRequest.getSort()).page(pageRequest));

        return  projects
                .map(project -> new ProjectDetail(
                        project.getProject(),
                        employeeProjectRepository.findBy(byProjectId(project.getId().getProjectId()), p -> p.project(EmployeeProject_.EMPLOYEE).all())
                                .stream()
                                .map(AssignHistory::of)
                                .toList()
                ));
    }

    @Override
    public List<EmployeeProjectResponse> getAllEmployeeInsideProject(Long projectId) {
        validateService.requireExistsProject(projectId);
        return employeeProjectRepository.findBy(byProjectId(projectId), p -> p.project(EmployeeProject_.EMPLOYEE).all())
                .stream()
                .map(EmployeeProjectResponse::toEmployeeProjectResponse)
                .toList();
    }

    @Override
    @Transactional
    public ProjectResponse createNewProject(CreateNewProjectRequest request) {
        Project project = MappingUtil.toProject(request);

        return projectToProjectResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(UpdateProjectStatusRequest request) {

        // check if the project exists or not, get the employees list along if the project exist
        Project project = projectRepository.findBy(ProjectSpecifications.byProjectId(request.id()), p -> p.project(Project_.EMPLOYEES).first())
                .orElseThrow(() -> new ProjectException(request.id()));

        // could not update FINISHED projects
        if (project.getState().equals(FINISHED)) {
            throw new ProjectException("Could not update project, project already finished ");
        }
        // the new state must be greater than the current state of the project
        if (request.newState().equals(project.getState())) {
            throw new ProjectException("New state is not valid, project already in that state ");
        }

        // update to INITIATION state is not valid
        if (request.newState().equals(INITIATION)) {
            throw new ProjectException("New state %s id not valid, project already in %s".formatted(request.newState(), project.getState()));
        } else if (request.newState().equals(ON_GOING)) {
            // update to ON_GOING state requires the actual start date
            if (request.actualStartDate() == null)
                throw new ProjectException("Actual start date is required");
            project.setActualStartDate(request.actualStartDate());
        } else if (request.newState().equals(FINISHED)) {
            // update to FINISHED state requires the actual finish date
            if (request.actualFinishDate() == null)
                throw new ProjectException("Actual finished date is required");
            project.setActualFinishDate(request.actualFinishDate());
        }
        project.setState(request.newState());
        // if the project is updated to finished then decrease the current effort of all employees inside the project
        if (request.newState().equals(FINISHED)) {
            project.getEmployees()
                    .forEach(e -> employeeRepository.updateCurrentEffortById(e.getId().getEmployeeId(), (-e.getEffort())));
        }
        return projectToProjectResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public List<EmployeeProjectResponse> assignEmployeeToProject(AssignEmployeeToProjectRequest request) {

        // check if the project exists or not, get the employees list along if the project exist
        Project project = projectRepository.findBy(ProjectSpecifications.byProjectId(request.projectId()), p -> p.project("employees.employee").first())
                .orElseThrow(() -> new ProjectException("Could not find project " + request.projectId()));

        // list of employees already assigned to the project
        List<EmployeeProjectResponse> alreadyInside = project.getEmployees()
                .stream()
                .map(EmployeeProjectResponse::toEmployeeProjectResponse)
                .collect(Collectors.toList());

        // remove already assigned employees from the request then perform the assignment
        List<EmployeeProjectResponse> list = request.assignList()
                .stream()
                .filter(ep -> alreadyInside.stream().noneMatch(e -> e.employeeId().equals(ep.employeeId())))
                .map(ep -> {
                    // check if the employee exists or not and whether their effort exceeds 100% or not
                    if (!employeeRepository.exists(byId(ep.employeeId()).and(byCurrentEffortLessThanOrEqualTo(100 - ep.effort())))) {
                        throw new ProjectException("Employee doesn't exists or effort exceeds 100%: " + ep );
                    }
                    // create a new record in the employee project table
                    EmployeeProject saved = employeeProjectRepository.save(new EmployeeProject(employeeRepository.getReferenceById(ep.employeeId()), project, ep.effort()));
                    // update the current effort for employee after assigning them to a project
                    employeeRepository.updateCurrentEffortById(ep.employeeId(), ep.effort());
                    return saved;
                })
                .map(EmployeeProjectResponse::toEmployeeProjectResponse).toList();

        // add the newly assigned employees list to the already assigned list and return
        alreadyInside.addAll(list);

        return alreadyInside;
    }
}
