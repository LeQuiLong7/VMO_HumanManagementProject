package com.lql.humanresourcedemo.service.admin;


import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.dto.response.admin.EmployeeProjectResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.exception.model.tech.TechException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectRepository;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectSpecifications;
import com.lql.humanresourcedemo.repository.project.ProjectRepository;
import com.lql.humanresourcedemo.repository.project.ProjectSpecifications;
import com.lql.humanresourcedemo.repository.salary.SalaryRaiseRequestRepository;
import com.lql.humanresourcedemo.repository.salary.SalaryRaiseSpecifications;
import com.lql.humanresourcedemo.repository.tech.EmployeeTechRepository;
import com.lql.humanresourcedemo.repository.tech.TechRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.lql.humanresourcedemo.enumeration.ProjectState.*;
import static com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications.*;
import static com.lql.humanresourcedemo.repository.project.EmployeeProjectSpecifications.byProjectId;
import static com.lql.humanresourcedemo.repository.tech.EmployeeTechSpecifications.byEmployeeId;
import static com.lql.humanresourcedemo.repository.tech.EmployeeTechSpecifications.byTechId;
import static com.lql.humanresourcedemo.utility.HelperUtility.*;
import static com.lql.humanresourcedemo.utility.MappingUtility.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SalaryRaiseRequestRepository salaryRepository;
    private final EmployeeTechRepository employeeTechRepository;
    private final EmployeeProjectRepository employeeProjectRepository;
    private final TechRepository techRepository;
    private final ProjectRepository projectRepository;

    @Override
    public Page<GetProfileResponse> getAllEmployee(Pageable pageRequest) {
        return employeeRepository.findAll(pageRequest).map(MappingUtility::employeeToProfileResponse);

    }

    @Override
    public Page<GetProfileResponse> getAllPM(Pageable pageRequest) {
        return employeeRepository.findBy(byRole(Role.PM), p -> p.sortBy(pageRequest.getSort()).page(pageRequest))
                .map(MappingUtility::employeeToProfileResponse);
    }

    @Override
    public Page<ProjectResponse> getAllProject(Pageable pageRequest) {
        return projectRepository.findAll(pageRequest).map(MappingUtility::projectToProjectResponse);

    }

    @Override
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Pageable pageRequest) {
        return salaryRepository.findAll(pageRequest).map(MappingUtility::salaryRaiseRequestToResponse);

    }

    @Override
    public Page<Tech> getAllTech(Pageable pageRequest) {
        return techRepository.findAll(pageRequest);
    }

    @Override
    public TechStackResponse getTechStackByEmployeeId(Long empId) throws EmployeeException {
        requiredExistsEmployee(empId);

        List<EmployeeTech> tech = employeeTechRepository.findBy(byEmployeeId(empId), p -> p.project("tech").all());

        return new TechStackResponse(
                empId,
                tech.stream()
                        .map(TechInfo::of)
                        .toList()
        );
    }


    @Override
    public List<EmployeeProjectResponse> getAllEmployeeInsideProject(Long projectId) {
        requiredExistsProject(projectId);
        return employeeProjectRepository.findBy(
                        byProjectId(projectId),
                        p -> p.project("employee").all())
                .stream()
                .map(EmployeeProjectResponse::toEmployeeProjectResponse)
                .toList();
    }


    @Override
    @Transactional
    public Page<ProjectDetail> getAllProjectsByEmployeeId(Long employeeId, Pageable pageRequest) throws EmployeeException {

        requiredExistsEmployee(employeeId);
        Page<EmployeeProject> projects = employeeProjectRepository.findBy(EmployeeProjectSpecifications.byEmployeeId(employeeId), p -> p.project("project").sortBy(pageRequest.getSort()).page(pageRequest));

        return projects
                .map(project -> new ProjectDetail(
                        project.getProject(),
                        employeeProjectRepository.findBy(byProjectId(project.getId().getProjectId()), p -> p.project("employee").all())
                                .stream()
                                .map(AssignHistory::of)
                                .toList()
                ));
    }

    @Override
    @Transactional
    public GetProfileResponse createNewEmployee(CreateNewEmployeeRequest request) {


        String email = buildEmail(request.firstName(), request.lastName());
        if (employeeRepository.existsByEmail(email)) {
            int count = employeeRepository.countByEmailLike(buildEmailWithWildcard(email));
            email = emailWithIdentityNumber(email, count);
        }
        String password = UUID.randomUUID().toString();

        Employee e = toEmployee(request, employeeRepository.getReferenceById(request.managedBy()), email, passwordEncoder.encode(password));

        employeeRepository.save(e);

        mailService.sendEmail(e.getPersonalEmail(), "[COMPANY] - WELCOME NEW EMPLOYEE", buildWelcomeMailMessage(e.getFirstName() + " " + e.getLastName(), e.getEmail(), password));

        return employeeToProfileResponse(e);
    }

    @Override
    @Transactional
    public SalaryRaiseResponse handleSalaryRaiseRequest(Long adminId, HandleSalaryRaiseRequest handleRequest) throws SalaryRaiseException {

        SalaryRaiseRequest raiseRequest = salaryRepository.findBy(SalaryRaiseSpecifications.byId(handleRequest.requestId()), p -> p.project("employee").first())
                .orElseThrow(() -> new SalaryRaiseException("Raise request not found"));

        if (raiseRequest.getStatus() != SalaryRaiseRequestStatus.PROCESSING) {
            throw new SalaryRaiseException("Raise request already handled");
        }
        if (handleRequest.status() == SalaryRaiseRequestStatus.PARTIALLY_ACCEPTED && handleRequest.newSalary() == null) {
            throw new SalaryRaiseException("New salary is required");
        }

        Double newSalary = switch (handleRequest.status()) {
            case PROCESSING -> throw new SalaryRaiseException("New status is not valid");
            case REJECTED -> raiseRequest.getCurrentSalary();
            case FULLY_ACCEPTED -> raiseRequest.getExpectedSalary();
            case PARTIALLY_ACCEPTED -> handleRequest.newSalary();
        };

        raiseRequest.setStatus(handleRequest.status());
        raiseRequest.setNewSalary(newSalary);
        raiseRequest.setApprovedBy(employeeRepository.getReferenceById(adminId));
        salaryRepository.save(raiseRequest);

        employeeRepository.updateSalaryById(raiseRequest.getEmployee().getId(), newSalary);

        mailService.sendEmail(raiseRequest.getEmployee().getPersonalEmail(),
                "[COMPANY] - YOUR SALARY RAISE REQUEST HAS BEEN PROCESSED",
                buildSalaryProcessedMail(raiseRequest.getEmployee().getFirstName(), raiseRequest));

        return salaryRaiseRequestToResponse(raiseRequest);
    }

    @Override
    @Transactional
    public TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request) {

        requiredExistsEmployee(request.employeeId());
        request.techStacks()
                .forEach(t -> {
                            if (!techRepository.existsById(t.techId()))
                                throw new TechException("Tech id %s not found".formatted(t.techId()));
                        }
                );

        request.techStacks().forEach(s -> {
            if (employeeTechRepository.exists(byEmployeeId(request.employeeId()).and(byTechId(s.techId())))) {
                employeeTechRepository.updateYearOfExperienceByEmployeeIdAndTechId(request.employeeId(), s.techId(), s.yearOfExperience());
            } else {
                employeeTechRepository.save(
                        new EmployeeTech(employeeRepository.getReferenceById(request.employeeId()), techRepository.getReferenceById(s.techId()), s.yearOfExperience())
                );
            }
        });

        List<EmployeeTech> tech = employeeTechRepository.findBy(byEmployeeId(request.employeeId()), p -> p.project("tech").all());

        return new TechStackResponse(
                request.employeeId(),
                tech.stream()
                        .map(TechInfo::of)
                        .toList()
        );
    }

    @Override
    @Transactional
    public ProjectResponse createNewProject(CreateNewProjectRequest request) {
        Project project = MappingUtility.toProject(request);

        return projectToProjectResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public ProjectResponse updateProject(UpdateProjectStatusRequest request) {

        Project project = projectRepository.findBy(ProjectSpecifications.byProjectId(request.id()), p -> p.project("employees").first())
                .orElseThrow(() -> new ProjectException(request.id()));


        if (project.getState().equals(FINISHED)) {
            throw new ProjectException("Could not update project, project already finished ");
        }

        if (request.newState().equals(project.getState())) {
            throw new ProjectException("New state is not valid, project already in that state ");
        }

        if (request.newState().equals(INITIATION)) {
            throw new ProjectException("New state %s id not valid, project already in %s".formatted(request.newState(), project.getState()));
        } else if (request.newState().equals(ON_GOING)) {
            if (request.actualStartDate() == null)
                throw new ProjectException("Actual start date is required");
            project.setActualStartDate(request.actualStartDate());
        } else if (request.newState().equals(FINISHED)) {
            if (request.actualFinishDate() == null)
                throw new ProjectException("Actual finished date is required");
            project.setActualFinishDate(request.actualFinishDate());
        }
        project.setState(request.newState());
        if (request.newState().equals(FINISHED)) {
            project.getEmployees()
                    .forEach(e -> employeeRepository.updateCurrentEffortById(e.getId().getEmployeeId(), (-e.getEffort())));
        }
        return projectToProjectResponse(projectRepository.save(project));
    }

    @Override
    @Transactional
    public List<EmployeeProjectResponse> assignEmployeeToProject(AssignEmployeeToProjectRequest request) {

        Project project = projectRepository.findBy(ProjectSpecifications.byProjectId(request.projectId()), p -> p.project("employees.employee").first())
                .orElseThrow(() -> new ProjectException("Could not find project " + request.projectId()));

        List<EmployeeProjectResponse> alreadyInside = project.getEmployees().stream()
                .map(EmployeeProjectResponse::toEmployeeProjectResponse)
                .collect(Collectors.toList());

        List<EmployeeProjectResponse> list = request.assignList()
                .stream()
                .filter(ep -> alreadyInside.stream().noneMatch(e -> e.employeeId().equals(ep.employeeId())))
                .map(ep -> {
                    if (!employeeRepository.exists(byId(ep.employeeId()).and(byCurrentEffortLessThanOrEqualTo(100 - ep.effort())))) {
                        throw new ProjectException("Employee doesn't exists or effort exceeds 100%: " + ep );
                    }
                    EmployeeProject saved = employeeProjectRepository.save(new EmployeeProject(employeeRepository.getReferenceById(ep.employeeId()), project, ep.effort()));
                    employeeRepository.updateCurrentEffortById(ep.employeeId(), ep.effort());
                    return saved;
                })
                .map(EmployeeProjectResponse::toEmployeeProjectResponse).toList();

        alreadyInside.addAll(list);

        return alreadyInside;
    }

    private void requiredExistsEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException("Could not find employee " + employeeId);

        }
    }

    private void requiredExistsProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectException("Could not find project " + projectId);

        }
    }

//    private void validateCreateNewEmployeeRequest(CreateNewEmployeeRequest request) {
//        if (employeeRepository.existsByPersonalEmail(request.personalEmail())) {
//            throw new NewAccountException("Personal email %s already exists".formatted(request.personalEmail()));
//        }
//    }
}
