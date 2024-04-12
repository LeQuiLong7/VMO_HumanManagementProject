package com.lql.humanresourcedemo.service.admin;


import com.lql.humanresourcedemo.dto.model.employee.OnlyPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.exception.model.newaccount.NewAccountException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.repository.*;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.lql.humanresourcedemo.enumeration.ProjectState.*;
import static com.lql.humanresourcedemo.utility.ContextUtility.*;
import static com.lql.humanresourcedemo.utility.HelperUtility.*;
import static com.lql.humanresourcedemo.utility.MappingUtility.*;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SalaryRaiseRequestRepository salaryRepository;
    private final EmployeeTechRepository employeeTechRepository;
    private final EmployeeProjectRepository employeeProjectRepository;
    private final TechRepository techRepository;
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;


    @Transactional

    public GetProfileResponse createNewEmployee(CreateNewEmployeeRequest request) {
        validateCreateNewEmployeeRequest(request);

        Employee e = createNewEmployeeRequestToEmployee(request);

        e.setManagedBy(employeeRepository.getReferenceById(request.managedBy()));

        String email = buildEmail(request.firstName(), request.lastName());
        if (employeeRepository.existsByEmail(email)) {
            int count = employeeRepository.countByEmailLike(buildEmailWithWildcard(email));
            email = emailWithIdentityNumber(email, count);

        }
        e.setEmail(email);
        String password = UUID.randomUUID().toString();
        e.setPassword(passwordEncoder.encode(password));
        employeeRepository.save(e);


        mailService.sendEmail(e.getPersonalEmail(), "[COMPANY] - WELCOME NEW EMPLOYEE", buildWelcomeMailMessage(e.getFirstName() + " " + e.getLastName(), e.getEmail(), password));

        return employeeToProfileResponse(e);
    }
    @Transactional
    public SalaryRaiseResponse handleSalaryRaiseRequest(HandleSalaryRaiseRequest handleRequest) {

        SalaryRaiseRequest raiseRequest = salaryRepository.findById(handleRequest.requestId())
                .orElseThrow(() -> new SalaryRaiseException("Raise request doesn't exists"));

        Double newSalary = switch (handleRequest.status()) {
            case REJECTED, PROCESSING -> raiseRequest.getCurrentSalary();
            case FULLY_ACCEPTED -> raiseRequest.getExpectedSalary();
            case PARTIALLY_ACCEPTED -> handleRequest.newSalary();
        };



        raiseRequest.setStatus(handleRequest.status());
        raiseRequest.setNewSalary(newSalary);
        raiseRequest.setApprovedBy(employeeRepository.getReferenceById(getCurrentEmployeeId()));
        salaryRepository.save(raiseRequest);

        employeeRepository.updateSalaryById(raiseRequest.getEmployee().getId(), newSalary);

        OnlyPersonalEmailAndFirstName personalEmailAndFirstName = employeeRepository.findById(raiseRequest.getEmployee().getId(), OnlyPersonalEmailAndFirstName.class).get();
        mailService.sendEmail(personalEmailAndFirstName.personalEmail(),
                "[COMPANY] - YOUR SALARY RAISE REQUEST HAS BEEN PROCESSED",
                buildSalaryProcessedMail(personalEmailAndFirstName.firstName(), raiseRequest));

        return salaryRaiseRequestToResponse(raiseRequest);
    }

    @Transactional

    public TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request) {

        for (var s : request.techStacks()) {

            if (employeeTechRepository.existsByIdEmployeeIdAndIdTechId(request.employeeId(), s.techId())) {
                employeeTechRepository.updateYearOfExperienceByEmployeeIdAndTechId(request.employeeId(), s.techId(), s.yearOfExperience());
            } else {

                employeeTechRepository.save(
                        new EmployeeTech(
                                new EmployeeTech.EmployeeTechId(
                                        employeeRepository.getReferenceById(request.employeeId()),
                                        techRepository.getReferenceById(s.techId())),
                                s.yearOfExperience())
                );
            }

        }
        return new TechStackResponse(
                request.employeeId(),
                employeeTechRepository.findTechInfoByEmployeeId(request.employeeId())
                        .stream()
                        .map(MappingUtility::employeeTechDTOtoTechInfo)
                        .toList()
        );
    }
    @Transactional
    public ProjectResponse createNewProject(CreateNewProjectRequest request) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .expectedStartDate(request.expectedStartDate())
                .expectedFinishDate(request.expectedFinishDate())
                .state(INITIATION)
                .client(clientRepository.getReferenceById(request.clientId())).build();

        return projectToProjectResponse(projectRepository.save(project));
    }

    @Transactional

    public ProjectResponse updateProject(UpdateProjectStatusRequest request) {

        Project p = projectRepository.findById(request.id()).orElseThrow(
                () -> new ProjectException("Could not find project " + request.id())
        );


        if (p.getState().equals(FINISHED)) {
            throw new ProjectException("Could not update project, project already finished ");
        }

        if (request.newState().equals(p.getState())) {
            throw new ProjectException("New state is not valid, project already in that state ");
        }


        if (request.newState().equals(INITIATION)) {
            throw new ProjectException("New state %s id not valid, project already in %s".formatted(request.newState(), p.getState()));
        } else if (request.newState().equals(ON_GOING)) {
            if (request.actualStartDate() == null)
                throw new ProjectException("Actual start date is required");
            p.setActualStartDate(request.actualStartDate());
        } else if (request.newState().equals(FINISHED)) {
            if (request.actualFinishDate() == null)
                throw new ProjectException("Actual finished date is required");
            p.setActualFinishDate(request.actualFinishDate());
        }
        p.setState(request.newState());

        return projectToProjectResponse(projectRepository.save(p));
    }


    @Transactional
    public AssignEmployeeToProjectRequest assignEmployeeToProject(AssignEmployeeToProjectRequest request) {
        requiredExistsProject(request.projectId());
        request.employeeIds().forEach(this::requiredExistsEmployee);

        assignEmployeesToProject(request.projectId(), request.employeeIds());

        return new AssignEmployeeToProjectRequest(request.projectId(), employeeProjectRepository.getAllEmployeesAssignedByProjectId(request.projectId()));
    }
    public void assignEmployeesToProject(Long projectId, List<Long> employeeIds) {
        employeeIds.forEach(employeeId -> employeeProjectRepository.save(new EmployeeProject(new EmployeeProject.EmployeeProjectId(employeeRepository.getReferenceById(employeeId), projectRepository.getReferenceById(projectId)))));
    }

    private void requiredExistsProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectException("Could not find project " + projectId);

        }
    }

    private void requiredExistsEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException("Could not find employee " + employeeId);

        }
    }

    private void validateCreateNewEmployeeRequest(CreateNewEmployeeRequest request) {
        if (employeeRepository.existsByPersonalEmail(request.personalEmail())) {
            throw new NewAccountException("Personal email %s already exists".formatted(request.personalEmail()));
        }
    }
}
