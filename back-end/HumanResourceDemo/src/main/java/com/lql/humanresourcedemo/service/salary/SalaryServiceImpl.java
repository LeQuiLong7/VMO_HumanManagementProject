package com.lql.humanresourcedemo.service.salary;

import com.lql.humanresourcedemo.dto.model.employee.OnlySalary;
import com.lql.humanresourcedemo.dto.request.admin.HandleSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest_;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.salary.SalaryRaiseRequestRepository;
import com.lql.humanresourcedemo.repository.salary.SalaryRaiseSpecifications;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.util.MappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import static com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus.PROCESSING;
import static com.lql.humanresourcedemo.repository.salary.SalaryRaiseSpecifications.byEmployeeId;
import static com.lql.humanresourcedemo.repository.salary.SalaryRaiseSpecifications.byStatus;
import static com.lql.humanresourcedemo.util.HelperUtil.buildSalaryProcessedMail;
import static com.lql.humanresourcedemo.util.MappingUtil.salaryRaiseRequestToResponse;
import static com.lql.humanresourcedemo.util.MappingUtil.toSalaryRaiseRequest;

@Service
@RequiredArgsConstructor
public class SalaryServiceImpl implements SalaryService{
    private final SalaryRaiseRequestRepository salaryRepository;
    private final EmployeeRepository employeeRepository;
    private final ValidateService validateService;
    private final MailService mailService;
    @Override
    public SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request) {
        // can not create two salary raise request at a time, the previous request must be handled before
        // they can create another salary raise request
        if(salaryRepository.exists(byEmployeeId(employeeId).and(byStatus(PROCESSING)))) {
            throw new SalaryRaiseException("Already have a PROCESSING salary raise request");
        }

        OnlySalary currentSalary = employeeRepository.findById(employeeId, OnlySalary.class)
                .orElseThrow(() -> new EmployeeException(employeeId));

        // expected salary must be greater than their current salary
        if (request.expectedSalary() <= currentSalary.currentSalary()) {
            throw new SalaryRaiseException("Expected salary is lower than current salary");
        }
        SalaryRaiseRequest raiseRequest = toSalaryRaiseRequest(request, currentSalary.currentSalary(), employeeRepository.getReferenceById(employeeId));

        return salaryRaiseRequestToResponse(salaryRepository.save(raiseRequest));
    }

    @Override
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Long employeeId, Role role, Pageable pageRequest) {
        validateService.requireExistsEmployee(employeeId);

        Page<SalaryRaiseRequest> raiseRequestList = switch (role) {
            case EMPLOYEE -> salaryRepository.findBy(byEmployeeId(employeeId), p -> p.sortBy(pageRequest.getSort()).page(pageRequest));
            case PM -> throw new AccessDeniedException("You are now allow to view salary raise list");
            case ADMIN -> salaryRepository.findAll(pageRequest);
        };
        return raiseRequestList.map(MappingUtil::salaryRaiseRequestToResponse);
    }

    @Override
    public SalaryRaiseResponse handleSalaryRaiseRequest(Long adminId, HandleSalaryRaiseRequest handleRequest) {
        // check whether the raise request exists for not
        SalaryRaiseRequest raiseRequest = salaryRepository.findBy(SalaryRaiseSpecifications.byId(handleRequest.requestId()), p -> p.project(SalaryRaiseRequest_.EMPLOYEE).first())
                .orElseThrow(() -> new SalaryRaiseException("Raise request not found"));

        // throw an exception if the request is already handled (status is not PROCESSING)
        if (raiseRequest.getStatus() != SalaryRaiseRequestStatus.PROCESSING) {
            throw new SalaryRaiseException("Raise request already handled");
        }
        // if the raise request is partially accepted then the new salary is required in the handle request
        if (handleRequest.status() == SalaryRaiseRequestStatus.PARTIALLY_ACCEPTED) {
            if(handleRequest.newSalary() == null)
                throw new SalaryRaiseException("New salary is required");
            // new salary must be greater than their current salary
            if(handleRequest.newSalary() <= raiseRequest.getCurrentSalary()) {
                throw new SalaryRaiseException("New salary must be greater than current salary");
            }
        }

        Double newSalary = switch (handleRequest.status()) {
            // new status PROCESSING is not valid
            case PROCESSING -> throw new SalaryRaiseException("New status is not valid");
            // if the raise request is rejected, then their new salary will be the current salary
            case REJECTED -> raiseRequest.getCurrentSalary();
            // if the raise request is partially accepted, then their new salary will be the new salary in the handle request
            case FULLY_ACCEPTED -> raiseRequest.getExpectedSalary();
            // if the raise request is fully accepted, then their new salary will be their expected salary in the raise request
            case PARTIALLY_ACCEPTED -> handleRequest.newSalary();
        };

        raiseRequest.setStatus(handleRequest.status());
        raiseRequest.setNewSalary(newSalary);
        raiseRequest.setApprovedBy(employeeRepository.getReferenceById(adminId));

        // update the raise request
        salaryRepository.save(raiseRequest);
        // update the current salary in employee table
        employeeRepository.updateSalaryById(raiseRequest.getEmployee().getId(), newSalary);
        // send a mail notify that the raise request has been handled
        mailService.sendEmail(raiseRequest.getEmployee().getPersonalEmail(),
                "[COMPANY] - YOUR SALARY RAISE REQUEST HAS BEEN PROCESSED",
                buildSalaryProcessedMail(raiseRequest.getEmployee().getFirstName(), raiseRequest));
        // map to response
        return salaryRaiseRequestToResponse(raiseRequest);
    }
}
