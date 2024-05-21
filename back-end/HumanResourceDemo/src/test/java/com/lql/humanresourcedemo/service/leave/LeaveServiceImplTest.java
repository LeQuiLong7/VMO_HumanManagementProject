package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.model.employee.OnLyLeaveDays;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {
    private final Pageable pageable = Pageable.unpaged();
    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private ValidateService validateService;
    @Mock
    private MailService mailService;
    @Mock
    private EmployeeRepository employeeRepository;

    private LeaveService leaveService;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRepository, validateService, mailService, employeeRepository);
    }

    @Test
    void testCreateLeaveRequest_Success() {
        // Given
        LeaveRequestt leaveRequest = new LeaveRequestt(LocalDate.now().plusDays(1), "", LeaveType.PAID);
        OnLyLeaveDays leaveDays = new OnLyLeaveDays((byte) 2);
        long employeeId = 1L;

        when(employeeRepository.findById(eq(employeeId), eq(OnLyLeaveDays.class)))
                .thenReturn(Optional.of(leaveDays));
        when(employeeRepository.getReferenceById(employeeId))
                .thenReturn(new Employee());
        when(leaveRepository.exists(any(Specification.class)))
                .thenReturn(false);

        when(leaveRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        LeaveResponse response = assertDoesNotThrow(() -> leaveService.createLeaveRequest(employeeId, leaveRequest));

        // Then
        assertNotNull(response);
        verify(validateService, times(1)).requireExistsEmployee(employeeId);
        verify(leaveRepository, times(1)).save(any(LeaveRequest.class));
        verify(employeeRepository, times(1)).getReferenceById(employeeId);
    }

    @Test
    void testCreateLeaveRequest_NotEnoughLeaveDays() {
        // Given
        LeaveRequestt leaveRequest = new LeaveRequestt(LocalDate.now().plusDays(1), "", LeaveType.PAID);

        OnLyLeaveDays leaveDays = new OnLyLeaveDays((byte) 0);
        long employeeId = 1L;

        when(employeeRepository.findById(eq(employeeId), eq(OnLyLeaveDays.class)))
                .thenReturn(Optional.of(leaveDays));

        assertThrows(
                LeaveRequestException.class,
                () -> leaveService.createLeaveRequest(employeeId, leaveRequest),
                "Requesting a paid leave day but not enough leave day left"

        );

        verify(validateService, times(1)).requireExistsEmployee(employeeId);
        verify(employeeRepository, times(1)).findById(eq(employeeId), eq(OnLyLeaveDays.class));
    }

    @Test
    void testCreateLeaveRequest_AlreadyExistsOnSameDate() {
        LeaveRequestt leaveRequest = new LeaveRequestt(LocalDate.now().plusDays(1), "", LeaveType.PAID);

        OnLyLeaveDays leaveDays = new OnLyLeaveDays((byte) 2);
        long employeeId = 1L;

        when(employeeRepository.findById(eq(employeeId), eq(OnLyLeaveDays.class)))
                .thenReturn(Optional.of(leaveDays));
        when(leaveRepository.exists(any(Specification.class)))
                .thenReturn(true);

        assertThrows(
                LeaveRequestException.class,
                () -> leaveService.createLeaveRequest(1L, leaveRequest),
                "Already have a leave request on that date"
        );

        verify(validateService, times(1)).requireExistsEmployee(1L);
        verify(employeeRepository, times(1)).findById(eq(employeeId), eq(OnLyLeaveDays.class));
        verify(leaveRepository, times(1)).exists(any(Specification.class));

    }

    @Test
    void testGetAllLeaveRequest_AsEmployee() {
        // Given
        long employeeId = 1L;
        Employee e =  Employee.builder().id(employeeId).build();
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(e);

        Page<LeaveRequest> page = new PageImpl<>(List.of(leaveRequest));

        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(page);

        // When
        Page<LeaveResponse> response = leaveService.getAllLeaveRequest(employeeId, Role.EMPLOYEE, Pageable.unpaged());

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(validateService, times(1)).requireExistsEmployee(employeeId);
        verify(leaveRepository, times(1)).findBy(any(Specification.class), any());
    }

    @Test
    void testGetAllLeaveRequest_AsPM() {
        // Given
        long employeeId = 1L;
        Employee e =  Employee.builder().id(employeeId).build();
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(e);

        Page<LeaveRequest> page = new PageImpl<>(List.of(leaveRequest));

        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(page);

        // When
        Page<LeaveResponse> response = leaveService.getAllLeaveRequest(employeeId, Role.PM, Pageable.unpaged());

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(validateService, times(1)).requireExistsEmployee(employeeId);
        verify(leaveRepository, times(1)).findBy(any(Specification.class), any());
    }


    @Test
    void testGetAllLeaveRequest_AsADMIN() {
        // Given
        long employeeId = 1L;
        Employee e =  Employee.builder().id(employeeId).build();
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(e);

        assertThrows(LeaveRequestException.class,
                () -> leaveService.getAllLeaveRequest(employeeId, Role.ADMIN, pageable),
                "You are not allow to view leave requests"
        );


        verify(validateService, times(1)).requireExistsEmployee(employeeId);
        verifyNoInteractions(leaveRepository);
    }

    @Test
    void testGetLeaveRequestByDateAndEmployeeId() {
        long employeeId = 1L;
        Employee e =  Employee.builder().id(employeeId).build();
        LeaveRequest leaveRequest = new LeaveRequest();
        leaveRequest.setEmployee(e);

        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(leaveRequest));

        Optional<LeaveResponse> response = leaveService.getLeaveRequestByDateAndEmployeeId(employeeId, LocalDate.of(2024, 5, 20));

        assertTrue(response.isPresent());
        verify(leaveRepository).findBy(any(Specification.class), any());
    }

    @Test
    void handleLeaveRequest_RequestNotFound() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.ACCEPTED);
        long pmId = 1L;

        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.empty());
        assertThrows(
                LeaveRequestException.class,
                () -> leaveService.handleLeaveRequest(pmId, handleRequest),
                "Leave request %s can not be found".formatted(handleRequest.requestId())
        );

        verify(validateService, times(1)).requireExistsEmployee(pmId);
        verify(leaveRepository, times(1)).findBy(any(Specification.class), any());
    }

    @Test
    void handleLeaveRequest_StatusProcessingIsNotValid() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.PROCESSING);

        assertThrows(
                LeaveRequestException.class,
                () -> leaveService.handleLeaveRequest(1L, handleRequest),
                "Status " + handleRequest.status() + " is not valid"
        );
        verifyNoInteractions(validateService);
        verifyNoInteractions(leaveRepository);
    }

    @Test
    void handleLeaveRequest_SuccessRejected() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.REJECTED);

        Employee employee = Employee.builder()
                .id(1L)
                .managedBy(Employee.builder().id(2L).build())
                .build();
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .id(1L)
                .status(LeaveStatus.PROCESSING)
                .employee(employee)
                .type(LeaveType.UNPAID)
                .build();
        Long pmId = 2L;

        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(leaveRequest));

        LeaveResponse leaveResponse = leaveService.handleLeaveRequest(pmId, handleRequest);

        assertEquals(LeaveStatus.REJECTED, leaveResponse.status());
        verify(validateService, times(1)).requireExistsEmployee(pmId);
        verify(employeeRepository, times(0)).decreaseLeaveDaysBy1(any());
        verify(mailService, times(1)).sendEmail(any(), any(), any());
        verify(leaveRepository, times(1)).save(any());
    }

    @Test
    void handleLeaveRequest_SuccessAcceptedUnPaidLeave() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.ACCEPTED);

        Employee employee = Employee.builder()
                .id(1L)
                .managedBy(Employee.builder().id(2L).build())
                .build();
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .id(1L)
                .status(LeaveStatus.PROCESSING)
                .employee(employee)
                .type(LeaveType.UNPAID)
                .build();
        Long pmId = 2L;


        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(leaveRequest));

        LeaveResponse leaveResponse = leaveService.handleLeaveRequest(pmId, handleRequest);

        assertEquals(LeaveStatus.ACCEPTED, leaveResponse.status());
        assertEquals(LeaveType.UNPAID, leaveResponse.type());

        verify(validateService, times(1)).requireExistsEmployee(pmId);
        verify(mailService, times(1)).sendEmail(any(), any(), any());
        verify(leaveRepository, times(1)).save(any());
    }


    @Test
    void handleLeaveRequest_SuccessAcceptedPaidLeave() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.ACCEPTED);

        Employee employee = Employee.builder()
                .id(1L)
                .managedBy(Employee.builder().id(2L).build())
                .build();

        LeaveRequest leaveRequest = LeaveRequest.builder()
                .id(1L)
                .status(LeaveStatus.PROCESSING)
                .employee(employee)
                .type(LeaveType.PAID)
                .build();
        Long pmId = 2L;


        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(leaveRequest));

        LeaveResponse leaveResponse = leaveService.handleLeaveRequest(pmId, handleRequest);

        assertEquals(LeaveStatus.ACCEPTED, leaveResponse.status());
        assertEquals(LeaveType.PAID, leaveResponse.type());

        verify(employeeRepository, times(1)).decreaseLeaveDaysBy1(any());
        verify(mailService, times(1)).sendEmail(any(), any(), any());
        verify(leaveRepository, times(1)).save(any());
    }

}