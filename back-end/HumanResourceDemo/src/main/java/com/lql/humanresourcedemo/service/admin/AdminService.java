package com.lql.humanresourcedemo.service.admin;


import com.lql.humanresourcedemo.dto.model.TechStack;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewEmployeeRequest;
import com.lql.humanresourcedemo.dto.request.admin.HandleSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.admin.UpdateEmployeeTechStackRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeNotFoundException;
import com.lql.humanresourcedemo.exception.model.newaccount.PersonalEmailAlreadyExistsException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.EmployeeTechRepository;
import com.lql.humanresourcedemo.repository.SalaryRaiseRequestRepository;
import com.lql.humanresourcedemo.repository.TechRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.utility.ContextUtility;
import com.lql.humanresourcedemo.utility.MappingUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
        // TODO: handle exception
        SalaryRaiseRequest raiseRequest = salaryRepository.findById(handleRequest.requestId()).orElseThrow(RuntimeException::new);

        Double newSalary = switch (handleRequest.status()) {
            case REJECTED, PROCESSING -> raiseRequest.getCurrentSalary();
            case FULLY_ACCEPTED -> raiseRequest.getExpectedSalary();
            case PARTIALLY_ACCEPTED -> handleRequest.newSalary();
        };

        raiseRequest.setStatus(handleRequest.status());
        raiseRequest.setNewSalary(newSalary);
        raiseRequest.setApprovedBy(employeeRepository.getReferenceById(ContextUtility.getCurrentEmployeeId()));
        salaryRepository.save(raiseRequest);


        Employee e = raiseRequest.getEmployee();
        e.setCurrentSalary(newSalary);
        employeeRepository.save(e);

        return MappingUtility.salaryRaiseRequestToResponse(raiseRequest);
    }


    // TODO: update employee tech stack
    public TechStackResponse updateEmployeeTechStack(UpdateEmployeeTechStackRequest request) {

        List<EmployeeTech> stack = employeeTechRepository.findByIdEmployeeId(request.employeeId());


        for (var s : request.techStacks()) {

            stack.stream().filter(i -> i.getId().getTech().getId().equals(s.techId()))
                    .findFirst()
                    .ifPresentOrElse(employeeTech -> {
                        employeeTech.setYearOfExperience(s.yearOfExperience());
                        employeeTechRepository.save(employeeTech);
                    }, () -> {
                        employeeTechRepository.save(new EmployeeTech(new EmployeeTech.EmployeeTechId(employeeRepository.getReferenceById(request.employeeId()), techRepository.getReferenceById(s.techId())), s.yearOfExperience()));
                    });

        }


        return new TechStackResponse(
                request.employeeId(),
                employeeTechRepository.findTechInfoByEmployeeId(request.employeeId())
                        .stream()
                        .map(MappingUtility::employeeTechDTOtoTechInfo)
                        .toList()
        );
    }


    private void validateCreateNewEmployeeRequest(CreateNewEmployeeRequest request) {
        if (employeeRepository.existsByPersonalEmail(request.personalEmail())) {
            throw new PersonalEmailAlreadyExistsException(request.personalEmail(), ContextUtility.getCurrentEmployeeId());
        }
    }
}
