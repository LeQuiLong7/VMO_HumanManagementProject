package com.lql.humanresourcedemo.service.pm;


import com.lql.humanresourcedemo.dto.model.employee.OnlyPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveSpecifications;
import com.lql.humanresourcedemo.service.mail.MailService;
//import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications.*;
import static com.lql.humanresourcedemo.utility.HelperUtility.*;
import static com.lql.humanresourcedemo.utility.MappingUtility.leaveRequestToResponse;

@Service
@RequiredArgsConstructor
public class PMServiceImpl implements PMService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;
    private final MailService mailService;


    @Override
    @Transactional
    public List<Attendance> checkAttendance(Long pmId, CheckAttendanceRequest request) {
        List<Long> empIdsInManage = employeeRepository.findAllIdByManagedById(pmId);
        LocalDate now = LocalDate.now();

        request.attendanceDetails()
                .forEach(detail -> {
                    if (!employeeRepository.existsById(detail.employeeId())) {
                        throw new EmployeeException(detail.employeeId());
                    }
                    if (!empIdsInManage.contains(detail.employeeId())) {
                        throw new EmployeeException("You cannot give attendance to people who are not in your manage");
                    }
                    if (attendanceRepository.existsByEmployeeIdAndDate(detail.employeeId(), now)) {
                        throw new EmployeeException("Employee %s already have an attendance record".formatted(detail.employeeId()));

                    }
                });

        return request.attendanceDetails()
                .stream()
                .map(at -> MappingUtility.toAttendance(at, now, employeeRepository.getReferenceById(at.employeeId())))
                .map(attendanceRepository::save)
                .toList();
    }

    private LeaveResponse handleLeaveRequest(Long pmId, HandleLeaveRequest request) {
        if (request.status().equals(LeaveStatus.PROCESSING)) {
            throw new LeaveRequestException("Status " + request.status() + " is not valid");
        }

        LeaveRequest l = leaveRepository.findById(request.requestId())
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

        if (request.status().equals(LeaveStatus.ACCEPTED) && l.getType().equals(LeaveType.PAID))
            employeeRepository.decreaseLeaveDaysBy1(l.getEmployee().getId());

        OnlyPersonalEmailAndFirstName personalEmailAndFirstName = employeeRepository.findById(l.getEmployee().getId(), OnlyPersonalEmailAndFirstName.class).get();
        mailService.sendEmail(personalEmailAndFirstName.personalEmail(),
                "[COMPANY] - YOUR LEAVE REQUEST HAS BEEN PROCESSED",
                buildLeaveRequestProcessedMail(personalEmailAndFirstName.firstName(), l));

        return leaveRequestToResponse(l);
    }

    @Override
    @Transactional
    public List<LeaveResponse> handleLeaveRequest(Long pmId, List<HandleLeaveRequest> requests) {
        List<LeaveResponse> leaveResponses = new ArrayList<>();
        for (var leaveRequest : requests) {
            leaveResponses.add(handleLeaveRequest(pmId, leaveRequest));
        }
        return leaveResponses;
    }

    @Override
    public Page<GetProfileResponse> getAllEmployee(Long pmId, Pageable pageRequest) {
        requireExists(pmId);
        return employeeRepository.findBy(byPmId(pmId).or(byId(pmId)), p -> p.sortBy(pageRequest.getSort()).page(pageRequest))
                .map(MappingUtility::employeeToProfileResponse);
    }

    @Override
    public Page<LeaveResponse> getAllLeaveRequest(Long pmId, Pageable pageRequest) {
        requireExists(pmId);

        return leaveRepository.findBy(LeaveSpecifications.byPmId(pmId), p -> p.project("employee").sortBy(pageRequest.getSort()).page(pageRequest))
                .map(MappingUtility::leaveRequestToResponse);
    }


    private void requireExists(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException(employeeId);
        }
    }

}
