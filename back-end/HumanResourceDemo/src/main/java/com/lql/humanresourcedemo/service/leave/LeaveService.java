package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.model.employee.OnLyLeaveDays;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeNotFoundException;
import com.lql.humanresourcedemo.exception.model.leaverequest.NotEnoughLeaveDaysException;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.LeaveRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.lql.humanresourcedemo.utility.MappingUtility.*;

@Service
@RequiredArgsConstructor
public class LeaveService {
    private final EmployeeRepository employeeRepository;
    private final LeaveRepository leaveRepository;
    public LeaveResponse createLeaveRequest(LeaveRequestt request) {


        OnLyLeaveDays leaveDays = employeeRepository.findById(request.employeeId(), OnLyLeaveDays.class)
                .orElseThrow(() -> new EmployeeNotFoundException(request.employeeId()));




        if(request.type().equals(LeaveType.PAID) && leaveDays.leaveDays() < 1) {
            throw new NotEnoughLeaveDaysException("Requesting a paid leave day but not enough leave day left - Account id: " + request.employeeId());
        }

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(employeeRepository.getReferenceById(request.employeeId()))
                .date(request.leaveDate())
                .type(request.type())
                .status(LeaveStatus.PROCESSING)
                .reason(request.reason())
                .build();

        return leaveRequestToResponse(leaveRepository.save(leaveRequest));


    }
}
