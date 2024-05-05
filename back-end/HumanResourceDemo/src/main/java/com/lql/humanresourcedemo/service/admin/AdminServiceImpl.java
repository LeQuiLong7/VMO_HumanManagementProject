package com.lql.humanresourcedemo.service.admin;


import com.lql.humanresourcedemo.dto.request.admin.*;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.newaccount.NewAccountException;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.exception.model.tech.TechException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.repository.*;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static com.lql.humanresourcedemo.enumeration.ProjectState.*;
import static com.lql.humanresourcedemo.utility.HelperUtility.*;
import static com.lql.humanresourcedemo.utility.MappingUtility.*;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final SalaryRaiseRequestRepository salaryRepository;
    //    private final EmployeeTechRepository employeeTechRepository;
    private final EmployeeTechRepository employeeTechRepository;
    //    private final EmployeeProjectRepository employeeProjectRepository;
    private final EmployeeProjectRepository employeeProjectRepository;
    private final TechRepository techRepository;
    private final ProjectRepository projectRepository;
    private final ApplicationContext applicationContext;

    @Override
    public Page<GetProfileResponse> getAllEmployee(Pageable pageRequest) {
        return getAll(Employee.class, EmployeeRepository.class, MappingUtility::employeeToProfileResponse, pageRequest);

    }

    @Override
    public Page<GetProfileResponse> getAllPM(Pageable pageRequest) {
        return employeeRepository.findAllByRole(Role.PM, pageRequest).map(MappingUtility::employeeToProfileResponse);

    }

    @Override
    public Page<ProjectResponse> getAllProject(Pageable pageRequest) {
        return getAll(Project.class, ProjectRepository.class, MappingUtility::projectToProjectResponse, pageRequest);

    }

    @Override
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Pageable pageRequest) {
        return getAll(SalaryRaiseRequest.class, SalaryRaiseRequestRepository.class, MappingUtility::salaryRaiseRequestToResponse, pageRequest);

    }


    @Override
    public Page<Tech> getAllTech(Pageable pageRequest) {
        return getAll(Tech.class, TechRepository.class, Function.identity(), pageRequest);

    }


    @Override
    public TechStackResponse getTechStackByEmployeeId(Long empId) throws EmployeeException {
        requiredExistsEmployee(empId);
        return new TechStackResponse(
                empId,
                employeeTechRepository.findTechInfoByEmployeeId(empId)
        );
    }


    @Override
    public Page<GetProfileResponse> getAllEmployeeInsideProject(Long projectId, Pageable pageRequest) {


        Specification<EmployeeProject> specification = (root, query, criteriaBuilder) -> {
            root.fetch("employee");
            return
                    criteriaBuilder.equal(root.get("id").get("projectId"), projectId);
        };

        return employeeProjectRepository.findBy(specification, p -> p.page(pageRequest).map(EmployeeProject::getEmployee)
                .map(MappingUtility::employeeToProfileResponse));
//
//        return new AssignEmployeeToProjectRequest(projectId, list);
//
//
//
//        return employeeProjectRepository.findAllByIdProjectId(projectId, pageRequest)
//                .map(employeeProject -> employeeProject.getId().getEmployee())
//                .map(MappingUtility::employeeToProfileResponse);
    }


    @Override
    @Transactional
    public Page<ProjectDetail> getAllProjectsByEmployeeId(Long employeeId, Pageable pageRequest) throws EmployeeException {


        requiredExistsEmployee(employeeId);
        Specification<Employee> specification1 = (root, query, criteriaBuilder) -> {
            root.fetch("projects").fetch("project");
            return criteriaBuilder.equal(root.get("id"), employeeId);
        };
        Employee employee = employeeRepository.findBy(specification1, p -> p.oneValue());
        List<ProjectDetail> list = employee.getProjects().stream()
                .map(project -> {
                    Specification<EmployeeProject> specification = (root, query, criteriaBuilder) -> {
                        root.fetch("employee");
                        return criteriaBuilder.equal(root.get("id").get("projectId"), project.getProject().getId());
                    };

                    return new ProjectDetail(project.getProject(),
                            employeeProjectRepository.findAll(specification)
                                    .stream().map(ep ->
                                            new AssignHistory(ep.getEmployee().getId(),
                                                    ep.getEmployee().getLastName() + " " + ep.getEmployee().getFirstName(),
                                                    ep.getEmployee().getAvatarUrl(),
                                                    ep.getEmployee().getRole(),
                                                    ep.getCreatedAt(),
                                                    ep.getCreatedBy())).toList()


                    );
                }).toList();

        return new PageImpl<>(list);


//        projectRepository.

//        return employeeProjectRepository.findAllByIdEmployeeId(employeeId, pageRequest)
//                .map(employeeProject ->
//                        new ProjectDetail(employeeProject.getId().getProject(),
//                                employeeProjectRepository.getAssignHistoryByProjectId(employeeProject.getId().getProject().getId())
//                        ));
    }


    private <T, R extends PagingAndSortingRepository<T, ?>, V> Page<V> getAll(Class<T> clazz, Class<R> repoClass, Function<T, V> mappingFunction, Pageable pageRequest) {

        try {
            Method findAll = repoClass.getMethod("findAll", Pageable.class);

            return ((Page<T>) findAll.invoke(applicationContext.getBean(repoClass), pageRequest)).map(mappingFunction);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
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

    @Override
    @Transactional
    public SalaryRaiseResponse handleSalaryRaiseRequest(Long adminId, HandleSalaryRaiseRequest handleRequest) throws SalaryRaiseException {

        SalaryRaiseRequest raiseRequest = salaryRepository.findById(handleRequest.requestId())
                .orElseThrow(() -> new SalaryRaiseException("Raise request doesn't exists"));

        if (raiseRequest.getStatus() != SalaryRaiseRequestStatus.PROCESSING) {
            throw new SalaryRaiseException("Raise request already handled");
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

            if (employeeTechRepository.existsByIdEmployeeIdAndIdTechId(request.employeeId(), s.techId())) {
                employeeTechRepository.updateYearOfExperienceByEmployeeIdAndTechId(request.employeeId(), s.techId(), s.yearOfExperience());
            } else {
                employeeTechRepository.save(
                        new EmployeeTech(employeeRepository.getReferenceById(request.employeeId()), techRepository.getReferenceById(s.techId()), s.yearOfExperience())
                );
            }
        });

        Specification<EmployeeTech> specification = (root, query, criteriaBuilder) -> {

            root.fetch("tech");
            return criteriaBuilder.equal(root.get("id").get("employeeId"), request.employeeId());
        };


        List<EmployeeTech> all = employeeTechRepository.findAll(specification);

        return new TechStackResponse(
                request.employeeId(),
                all.stream().map(ep -> new TechInfo(ep.getTech().getId(), ep.getTech().getName(), ep.getYearOfExperience()))
                        .toList()
        );

//        return new TechStackResponse(
//                request.employeeId(),
//                employeeTechRepository.findTechInfoByEmployeeId(request.employeeId()));
    }

    @Override
    @Transactional
    public ProjectResponse createNewProject(CreateNewProjectRequest request) {
        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .expectedStartDate(request.expectedStartDate())
                .expectedFinishDate(request.expectedFinishDate())
                .state(INITIATION)
//                .client(clientRepository.getReferenceById(request.clientId()))
                .build();

        return projectToProjectResponse(projectRepository.save(project));
    }

    @Override
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

    @Override
    @Transactional
    public AssignEmployeeToProjectRequest assignEmployeeToProject(AssignEmployeeToProjectRequest request) {

        return assign2(request);
//        requiredExistsProject(request.projectId());
//        request.employeeIds().forEach(this::requiredExistsEmployee);
//
//
//        assignEmployeesToProject(request.projectId(), request.employeeIds());
//
//        return new AssignEmployeeToProjectRequest(request.projectId(), employeeProjectRepository.getAllEmployeesAssignedByProjectId(request.projectId()));
    }

    private AssignEmployeeToProjectRequest assign2(AssignEmployeeToProjectRequest request) {
        Long projectId = request.projectId();
        requiredExistsProject(request.projectId());


        Project referenceById = projectRepository.getReferenceById(projectId);

        request.employeeIds().forEach(this::requiredExistsEmployee);

        List<Long> employeeIds = request.employeeIds();


//        employeeIds.forEach(employeeId -> employeeProject2Repository.save(new EmployeeProject2(employeeRepository.getReferenceById(employeeId), referenceById)));


        Specification<EmployeeProject> specification = (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("id").get("projectId"), projectId);

        List<Long> list = employeeProjectRepository.findAll(specification).stream().map(ep -> ep.getId().getEmployeeId()).toList();

        return new AssignEmployeeToProjectRequest(projectId, list);

    }

//    public void assignEmployeesToProject(Long projectId, List<Long> employeeIds) {
//        employeeIds.forEach(employeeId -> employeeProjectRepository.save(new EmployeeProject(new EmployeeProject.EmployeeProjectId(employeeRepository.getReferenceById(employeeId), projectRepository.getReferenceById(projectId)))));
//    }

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
