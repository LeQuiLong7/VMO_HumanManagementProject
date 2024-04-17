package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.model.employee.OnLyLeaveDays;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.LeaveRepository;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private ValidateService validateService;
    private LeaveService leaveService;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(employeeRepository, leaveRepository, validateService);
    }

    @Test
    void testCreateLeaveRequest_UserNotFound() {
        Long employeeId = 1L;
        LeaveRequestt leaveRequestt = new LeaveRequestt(null, null, null);
        when(employeeRepository.findById(any(Long.class), eq(OnLyLeaveDays.class)))
                .thenReturn(Optional.empty());

        assertThrows(
                EmployeeException.class,
                () -> leaveService.createLeaveRequest(employeeId, leaveRequestt),
                "Could not find employee " + employeeId);


    }

    @Test
    void testCreateLeaveRequest_PaidLeaveWithoutEnoughDays() {
        Long employeeId = 1L;

        LeaveRequestt leaveRequestt = new LeaveRequestt(null, null, LeaveType.PAID);
        OnLyLeaveDays leaveDays = new OnLyLeaveDays(((byte) 0));
        when(employeeRepository.findById(any(Long.class), eq(OnLyLeaveDays.class)))
                .thenReturn(Optional.of(leaveDays));

        assertThrows(
                LeaveRequestException.class,
                () -> leaveService.createLeaveRequest(employeeId, leaveRequestt),
                "Requesting a paid leave day but not enough leave day left");

    }

    @Test
    void testCreateLeaveRequest_PaidLeaveWithEnoughDays() {

        Long employeeId = 1L;
        Employee employee = Employee.builder()
                .id(employeeId).build();
        OnLyLeaveDays leaveDays = new OnLyLeaveDays(((byte) 5));

        LeaveRequestt leaveRequestt = new LeaveRequestt(null, null, LeaveType.PAID);


        when(employeeRepository.findById(any(Long.class), eq(OnLyLeaveDays.class)))
                .thenReturn(Optional.of(leaveDays));
        when(employeeRepository.getReferenceById(any(Long.class)))
                .thenReturn(employee);
        when(leaveRepository.save(any(LeaveRequest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        LeaveResponse response = leaveService.createLeaveRequest(employeeId, leaveRequestt);

        assertAll(
                () -> assertEquals(leaveRequestt.leaveDate(), response.date()),
                () -> assertEquals(employeeId, response.employeeId()),
                () -> assertEquals(leaveRequestt.type(), response.type()),
                () -> assertEquals(LeaveStatus.PROCESSING, response.status())
        );

    }

}