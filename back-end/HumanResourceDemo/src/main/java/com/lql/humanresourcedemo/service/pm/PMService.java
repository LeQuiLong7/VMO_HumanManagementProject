package com.lql.humanresourcedemo.service.pm;


import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestNotFoundException;
import com.lql.humanresourcedemo.exception.model.leaverequest.StatusNotValidException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.repository.AttendanceRepository;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.LeaveRepository;
import com.lql.humanresourcedemo.utility.ContextUtility;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

import static com.lql.humanresourcedemo.utility.MappingUtility.*;

@Service
@RequiredArgsConstructor
public class PMService {

    private final AttendanceRepository attendanceRepository;
    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;
//    private final LeaveService leaveService;


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
    public LeaveResponse handleLeaveRequest(HandleLeaveRequest request) {

        LeaveRequest l = leaveRepository.findById(request.requestId())
                .orElseThrow(() -> new LeaveRequestNotFoundException("Leave request %s can not be found".formatted(request.requestId())));

        if(!request.status().equals(LeaveStatus.PROCESSING)) {
            throw new StatusNotValidException("Status " + request.status() + " is not valid");
        }

        l.setStatus(request.status());
        l.setApprovedBy(employeeRepository.getReferenceById(ContextUtility.getCurrentEmployeeId()));
        leaveRepository.save(l);


        employeeRepository.decreaseLeaveDaysBy1(l.getEmployee().getId());


        return leaveRequestToResponse(l);
    }
}
