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
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
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
    private LeaveService leaveService;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(employeeRepository, leaveRepository);
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

    @Test
    void testGetAllLeaveRequest() {
        Long employeeId = 1L;
        Employee employee = Employee.builder().id(1L).build();
        LeaveRequest leaveRequest = new LeaveRequest(1L, employee, null, null, null, null, null);

        when(leaveRepository.findAllByEmployeeId(any(Long.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(leaveRequest)));


        Page<LeaveResponse> response = leaveService.getAllLeaveRequest(employeeId, Pageable.unpaged());

        assertAll(
                () -> assertEquals(1, response.getTotalElements()),
                () -> assertEquals(1, response.getContent().size())
        );

    }


    @Test
    void testGetLeaveRequestByEmployeeIdAndDate_NotFound() {
        Long employeeId = 1L;
        LocalDate date = LocalDate.now();
        when(leaveRepository.findByEmployeeIdAndDate(employeeId, date))
                .thenReturn(Optional.empty());


        Optional<LeaveResponse> response = leaveService.getLeaveRequestByDateAndEmployeeId(employeeId, date);

        assertAll(
                () -> assertFalse(response.isPresent())
        );

    }

    @Test
    void testGetLeaveRequestByEmployeeIdAndDate_Found() {
        Long employeeId = 1L;
        LocalDate date = LocalDate.now();
        Employee employee = Employee.builder().id(1L).build();
        LeaveRequest leaveRequest = new LeaveRequest(1L, employee, date, null, null, null, null);


        when(leaveRepository.findByEmployeeIdAndDate(employeeId, date))
                .thenReturn(Optional.of(leaveRequest));


        Optional<LeaveResponse> response = leaveService.getLeaveRequestByDateAndEmployeeId(employeeId, date);

        assertAll(
                () -> assertTrue(response.isPresent()),
                () -> assertEquals(leaveRequest.getEmployee().getId(), response.get().employeeId()),
                () -> assertEquals(leaveRequest.getDate(), response.get().date())
        );

    }
}