package com.lql.humanresourcedemo.service.admin;


import com.lql.humanresourcedemo.dto.request.admin.CreateNewEmployeeRequest;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewProjectRequest;
import com.lql.humanresourcedemo.dto.request.admin.HandleSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.admin.UpdateEmployeeTechStackRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.enumeration.ProjectState;
import com.lql.humanresourcedemo.exception.model.newaccount.PersonalEmailAlreadyExistsException;
import com.lql.humanresourcedemo.exception.model.salaryraise.RaiseRequestNotFoundException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.repository.*;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.utility.MappingUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

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
    private final TechRepository techRepository;
    private final ClientRepository clientRepository;
    private final ProjectRepository projectRepository;


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


        mailService.sendEmail(e.getPersonalEmail(), "", buildWelcomeMailMessage(e.getFirstName() + " " + e.getLastName(), e.getEmail(), password));

        return employeeToProfileResponse(e);
    }

    ;


    public SalaryRaiseResponse handleSalaryRaiseRequest(HandleSalaryRaiseRequest handleRequest) {

        SalaryRaiseRequest raiseRequest = salaryRepository.findById(handleRequest.requestId())
                .orElseThrow(() -> new RaiseRequestNotFoundException("Raise request %s doesn't exists - Account id: %s".formatted(handleRequest.requestId(), getCurrentEmployeeId())));

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

        return salaryRaiseRequestToResponse(raiseRequest);
    }

    public TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request) {

        for (var s : request.techStacks()) {

            if(employeeTechRepository.existsByIdEmployeeIdAndIdTechId(request.employeeId(), s.techId())) {
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

    public ProjectResponse createNewProject(CreateNewProjectRequest request) {


        Project project = Project.builder()
                .name(request.name())
                .description(request.description())
                .expectedStartDate(request.expectedStartDate())
                .expectedFinishDate(request.expectedFinishDate())
                .state(ProjectState.INITIATION)
                .client(clientRepository.getReferenceById(request.clientId())).build();

        return projectToProjectResponse(projectRepository.save(project));
    }


    private void validateCreateNewEmployeeRequest(CreateNewEmployeeRequest request) {
        if (employeeRepository.existsByPersonalEmail(request.personalEmail())) {
            throw new PersonalEmailAlreadyExistsException(request.personalEmail(), getCurrentEmployeeId());
        }
    }
}
