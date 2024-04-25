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
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.AttendanceRepository;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.LeaveRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.lql.humanresourcedemo.utility.HelperUtility.buildLeaveRequestProcessedMail;
import static com.lql.humanresourcedemo.utility.HelperUtility.buildPageRequest;
import static com.lql.humanresourcedemo.utility.MappingUtility.leaveRequestToResponse;

@Service
@RequiredArgsConstructor
public class PMServiceImpl implements PMService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;
    private final MailService mailService;
    private final ValidateService validateService;


    @Override
    @Transactional
    public List<Attendance> checkAttendance(Long pmId, CheckAttendanceRequest request) {

        List<Long> empIdsInManage = employeeRepository.findAllIdByManagedById(pmId);
        LocalDate now = LocalDate.now();

        request.attendanceDetails()
                        .forEach(detail -> {

                            if(!employeeRepository.existsById(detail.employeeId())) {
                                throw new EmployeeException(detail.employeeId());
                            }
                            if(!empIdsInManage.contains(detail.employeeId())) {
                                throw new EmployeeException("You cannot give attendance to people who are not in your manage");
                            }
                            if(attendanceRepository.existsByEmployeeIdAndDate(detail.employeeId(), now)) {
                                throw new EmployeeException("Employee %s already have an attendance record".formatted(detail.employeeId()));

                            }
                        });

        return request.attendanceDetails()
                .stream()
                .map(attendanceDetail -> attendanceRepository.save(
                        Attendance.builder()
                                .employee(employeeRepository.getReferenceById(attendanceDetail.employeeId()))
                                .date(now)
                                .timeIn(attendanceDetail.timeIn())
                                .timeOut(attendanceDetail.timeOut())
                                .build()))
                .toList();
    }

    private LeaveResponse handleLeaveRequest(Long pmId,HandleLeaveRequest request) {
        if (request.status().equals(LeaveStatus.PROCESSING)) {
            throw new LeaveRequestException("Status " + request.status() + " is not valid");
        }

        LeaveRequest l = leaveRepository.findById(request.requestId())
                .orElseThrow(() -> new LeaveRequestException("Leave request %s can not be found".formatted(request.requestId())));

        if(l.getStatus() != LeaveStatus.PROCESSING) {
            throw new LeaveRequestException("Cannot change already handle request");

        }
        if(!l.getEmployee().getManagedBy().getId().equals(pmId)) {
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
        for(var leaveRequest : requests) {
            leaveResponses.add(handleLeaveRequest(pmId, leaveRequest));
        }
        return leaveResponses;
    }

    @Override
    public Page<GetProfileResponse> getAllEmployee(Long pmId, String page, String pageSize, List<String> properties, List<String> orders) {
        validateService.validatePageRequest(page, pageSize, properties, orders, Employee.class);

        Pageable pageRequest = buildPageRequest(Integer.parseInt(page), Integer.parseInt(pageSize), properties, orders, Employee.class);

        return employeeRepository.findAllIdByManagedById(pmId, pageRequest).map(MappingUtility::employeeToProfileResponse);
    }
    @Override
    public Page<LeaveResponse> getAllLeaveRequest(Long pmId, String page, String pageSize, List<String> properties, List<String> orders) {
        validateService.validatePageRequest(page, pageSize, properties, orders, LeaveRequest.class);

        Pageable pageRequest = buildPageRequest(Integer.parseInt(page), Integer.parseInt(pageSize), properties, orders, LeaveRequest.class);


        return leaveRepository.findAllByEmployeeManagedById(pmId, pageRequest).map(MappingUtility::leaveRequestToResponse);
    }

}
