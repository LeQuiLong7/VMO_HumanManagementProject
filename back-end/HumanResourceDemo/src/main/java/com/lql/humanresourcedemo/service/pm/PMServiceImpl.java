package com.lql.humanresourcedemo.service.pm;


import com.lql.humanresourcedemo.dto.model.employee.OnlyPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.repository.AttendanceRepository;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.LeaveRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.service.mail.MailServiceImpl;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;
import static com.lql.humanresourcedemo.utility.HelperUtility.buildLeaveRequestProcessedMail;
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
    public List<Attendance> checkAttendance(CheckAttendanceRequest request) {
        return request.attendanceDetails()
                .stream()
                .map(attendanceDetail -> attendanceRepository.save(Attendance.builder()
                        .employee(employeeRepository.getReferenceById(attendanceDetail.employeeId()))
                        .date(LocalDate.now())
                        .timeIn(attendanceDetail.timeIn())
                        .timeOut(attendanceDetail.timeOut())
                        .build()))
                .toList();
    }

    @Transactional
    @Override
    public LeaveResponse handleLeaveRequest(HandleLeaveRequest request) {

        LeaveRequest l = leaveRepository.findById(request.requestId())
                .orElseThrow(() -> new LeaveRequestException("Leave request %s can not be found".formatted(request.requestId())));

        if(request.status().equals(LeaveStatus.PROCESSING)) {
            throw new LeaveRequestException("Status " + request.status() + " is not valid for leave request %s".formatted(request.requestId()));
        }

        l.setStatus(request.status());
        l.setApprovedBy(employeeRepository.getReferenceById(getCurrentEmployeeId()));
        leaveRepository.save(l);

        if(request.status().equals(LeaveStatus.ACCEPTED) && l.getType().equals(LeaveType.PAID))
            employeeRepository.decreaseLeaveDaysBy1(l.getEmployee().getId());

        OnlyPersonalEmailAndFirstName personalEmailAndFirstName = employeeRepository.findById(l.getEmployee().getId(), OnlyPersonalEmailAndFirstName.class).get();
        mailService.sendEmail(personalEmailAndFirstName.personalEmail(),
                "[COMPANY] - YOUR LEAVE REQUEST HAS BEEN PROCESSED",
                buildLeaveRequestProcessedMail(personalEmailAndFirstName.firstName(), l));


        return leaveRequestToResponse(l);
    }
}
