package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.model.employee.OnLyLeaveDays;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest_;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveSpecifications;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.util.MappingUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

import static com.lql.humanresourcedemo.repository.leave.LeaveSpecifications.*;
import static com.lql.humanresourcedemo.util.HelperUtil.buildLeaveRequestProcessedMail;
import static com.lql.humanresourcedemo.util.MappingUtil.leaveRequestToResponse;
import static com.lql.humanresourcedemo.util.MappingUtil.toLeaveRequest;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService{

    private final LeaveRepository leaveRepository;
    private final ValidateService validateService;
    private final MailService mailService;
    private final EmployeeRepository employeeRepository;


    @Override
    public LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt request) {
        validateService.requireExistsEmployee(employeeId);
        // if requesting a paid leave day then the employee must have more than 1 leave days
        if(request.type().equals(LeaveType.PAID)) {
            OnLyLeaveDays leaveDays = employeeRepository.findById(employeeId, OnLyLeaveDays.class)
                    .orElseThrow(() -> new EmployeeException(employeeId));
            if(leaveDays.leaveDays() < 1)
                throw new LeaveRequestException("Requesting a paid leave day but not enough leave day left");
        }
        // can not create two leave requests on the same day
        if(leaveRepository.exists(LeaveSpecifications.byEmployeeId(employeeId).and(byDate(request.leaveDate())))) {
            throw new LeaveRequestException("Already have a leave request on that date");
        }
        LeaveRequest leaveRequest = toLeaveRequest(employeeRepository.getReferenceById(employeeId), request);

        return leaveRequestToResponse(leaveRepository.save(leaveRequest));
    }

    @Override
    public Page<LeaveResponse> getAllLeaveRequest(Long employeeId, Role role, Pageable pageRequest) {
        validateService.requireExistsEmployee(employeeId);

        Page<LeaveRequest> leaveRequestList = switch (role) {
            case EMPLOYEE ->  leaveRepository.findBy(byEmployeeId(employeeId), p -> p.sortBy(pageRequest.getSort()).page(pageRequest));
            case PM -> leaveRepository.findBy(byPmId(employeeId), p -> p.sortBy(pageRequest.getSort()).page(pageRequest));
            case ADMIN -> throw new LeaveRequestException("You are not allow to view leave requests");
        };

        return leaveRequestList.map(MappingUtil::leaveRequestToResponse);
    }

    @Override
    public Optional<LeaveResponse> getLeaveRequestByDateAndEmployeeId(Long employeeId, LocalDate date) {
        return leaveRepository.findBy(byEmployeeId(employeeId).and(byDate(date)), FluentQuery.FetchableFluentQuery::first).map(MappingUtil::leaveRequestToResponse);
    }

    @Override
    public LeaveResponse handleLeaveRequest(Long pmId, HandleLeaveRequest request) {
        if (request.status().equals(LeaveStatus.PROCESSING)) {
            throw new LeaveRequestException("Status " + request.status() + " is not valid");
        }
        validateService.requireExistsEmployee(pmId);


        LeaveRequest l = leaveRepository.findBy(LeaveSpecifications.byId(request.requestId()), p -> p.project(LeaveRequest_.EMPLOYEE).first())
                .orElseThrow(() -> new LeaveRequestException("Leave request could not be found"));

        if (l.getStatus() != LeaveStatus.PROCESSING) {
            throw new LeaveRequestException("Cannot change already handle request");

        }
        if (!l.getEmployee().getManagedBy().getId().equals(pmId)) {
            throw new LeaveRequestException("You cannot handle leave request %s: employee is not in your manage".formatted(l.getId()));

        }

        l.setStatus(request.status());
        l.setApprovedBy(employeeRepository.getReferenceById(pmId));
        leaveRepository.save(l);

        // if the leave request type if paid then decrease one leave day from the employee's leave days
        if (request.status().equals(LeaveStatus.ACCEPTED) && l.getType().equals(LeaveType.PAID))
            employeeRepository.decreaseLeaveDaysBy1(l.getEmployee().getId());

        Employee e = l.getEmployee();
        // send email notify that the leave request has been handled
        mailService.sendEmail(e.getPersonalEmail(),
                "[COMPANY] - YOUR LEAVE REQUEST HAS BEEN PROCESSED",
                buildLeaveRequestProcessedMail(e.getFirstName(), l));

        return leaveRequestToResponse(l);
    }
}
