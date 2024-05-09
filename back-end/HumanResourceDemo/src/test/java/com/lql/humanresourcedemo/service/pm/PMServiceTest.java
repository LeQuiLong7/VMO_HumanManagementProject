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
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PMServiceTest {
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private LeaveRepository leaveRepository;
    @Mock
    private MailService mailService;

    private PMService pmService;

    @BeforeEach
    void setUp() {
        pmService = new PMServiceImpl(attendanceRepository, employeeRepository, leaveRepository, mailService);
    }

    @Test
    void checkAttendance_UserNotFound() {
        Long empId1 = 1L;
        Long pmId = 3L;
        CheckAttendanceRequest request = new CheckAttendanceRequest(List.of(new CheckAttendanceRequest.AttendanceDetail(empId1, null, null)));

        when(employeeRepository.findAllIdByManagedById(pmId)).thenReturn(List.of(empId1));
        when(employeeRepository.existsById(empId1)).thenReturn(false);


        assertThrows(EmployeeException.class,
                () -> pmService.checkAttendance(pmId, request),
                "Could not find employee " + empId1);
    }

    @Test
    void checkAttendance_UserNotInManage() {
        Long empId1 = 1L;
        Long pmId = 3L;
        CheckAttendanceRequest request = new CheckAttendanceRequest(List.of(new CheckAttendanceRequest.AttendanceDetail(empId1, null, null)));

        when(employeeRepository.findAllIdByManagedById(pmId)).thenReturn(Collections.emptyList());
        when(employeeRepository.existsById(anyLong())).thenReturn(true);


        assertThrows(EmployeeException.class,
                () -> pmService.checkAttendance(pmId, request),
                "You cannot give attendance to people who are not in your manage");
    }

    @Test
    void checkAttendance_Success() {
        Long empId1 = 1L;
        Long pmId = 3L;
        LocalTime timeIn = LocalTime.now();
        LocalTime timeOut = LocalTime.now();
        CheckAttendanceRequest request = new CheckAttendanceRequest(
                List.of(
                        new CheckAttendanceRequest.AttendanceDetail(empId1, timeIn, timeOut)));

        when(employeeRepository.findAllIdByManagedById(pmId)).thenReturn(List.of(empId1));
        when(employeeRepository.existsById(anyLong())).thenReturn(true);
        when(attendanceRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(employeeRepository.getReferenceById(anyLong()))
                .thenAnswer(
                        invocation ->
                                Employee.builder()
                                        .id(invocation.getArgument(0))
                                        .build());

        List<Attendance> response = pmService.checkAttendance(pmId, request);

        verify(employeeRepository, times(1)).findAllIdByManagedById(pmId);
        verify(employeeRepository, times(1)).existsById(empId1);
        verify(attendanceRepository, times(1)).save(any(Attendance.class));
        verify(employeeRepository, times(1)).getReferenceById(empId1);

        assertAll(
                () -> assertEquals(1, response.size()),
                () -> assertEquals(timeIn, response.get(0).getTimeIn()),
                () -> assertEquals(timeOut, response.get(0).getTimeOut())
        );
    }

    @Test
    void handleLeaveRequest_RequestNotFound() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.ACCEPTED);

        when(leaveRepository.findById(handleRequest.requestId())).thenReturn(Optional.empty());


        assertThrows(
                LeaveRequestException.class,
                () -> pmService.handleLeaveRequest(1L, List.of(handleRequest)),
                "Leave request %s can not be found".formatted(handleRequest.requestId())
        );
    }

    @Test
    void handleLeaveRequest_StatusProcessingIsNotValid() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.PROCESSING);

        assertThrows(
                LeaveRequestException.class,
                () -> pmService.handleLeaveRequest(1L, List.of(handleRequest)),
                "Status " + handleRequest.status() + " is not valid"
        );
    }

    @Test
    void handleLeaveRequest_SuccessRejected() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.REJECTED);
        OnlyPersonalEmailAndFirstName onlyPersonalEmailAndFirstName = new OnlyPersonalEmailAndFirstName("", "");

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

        when(leaveRepository.findById(handleRequest.requestId()))
                .thenReturn(Optional.of(leaveRequest));

        when(employeeRepository.findById(anyLong(), eq(OnlyPersonalEmailAndFirstName.class))).thenReturn(Optional.of(onlyPersonalEmailAndFirstName));

        pmService.handleLeaveRequest(2L, List.of(handleRequest));

        verify(employeeRepository, times(0)).decreaseLeaveDaysBy1(any());
        verify(mailService, times(1)).sendEmail(any(), any(), any());
        verify(leaveRepository, times(1)).save(any());
    }

    @Test
    void handleLeaveRequest_SuccessAcceptedUnPaidLeave() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.ACCEPTED);
        OnlyPersonalEmailAndFirstName onlyPersonalEmailAndFirstName = new OnlyPersonalEmailAndFirstName("", "");

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

        when(leaveRepository.findById(handleRequest.requestId()))
                .thenReturn(Optional.of(leaveRequest));

        when(employeeRepository.findById(anyLong(), eq(OnlyPersonalEmailAndFirstName.class))).thenReturn(Optional.of(onlyPersonalEmailAndFirstName));

        pmService.handleLeaveRequest(2L, List.of(handleRequest));

        verify(employeeRepository, times(0)).decreaseLeaveDaysBy1(any());
        verify(mailService, times(1)).sendEmail(any(), any(), any());
        verify(leaveRepository, times(1)).save(any());
    }


    @Test
    void handleLeaveRequest_SuccessAcceptedPaidLeave() {
        HandleLeaveRequest handleRequest = new HandleLeaveRequest(1L, LeaveStatus.ACCEPTED);
        OnlyPersonalEmailAndFirstName onlyPersonalEmailAndFirstName = new OnlyPersonalEmailAndFirstName("", "");

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

        when(leaveRepository.findById(handleRequest.requestId()))
                .thenReturn(Optional.of(leaveRequest));
        when(employeeRepository.findById(anyLong(), eq(OnlyPersonalEmailAndFirstName.class)))
                .thenReturn(Optional.of(onlyPersonalEmailAndFirstName));

        pmService.handleLeaveRequest(2L, List.of(handleRequest));

        verify(employeeRepository, times(1)).decreaseLeaveDaysBy1(any());
        verify(mailService, times(1)).sendEmail(any(), any(), any());
        verify(leaveRepository, times(1)).save(any());
    }
    Pageable pageable = Pageable.ofSize(10);


    @Test
    void getAllEmployeeTest() {
        Long pmId = 2L;
        Employee employee = Employee.builder().id(1L).build();
        when(employeeRepository.existsById(pmId))
                .thenReturn(true);

//        when(employeeRepository.findAllIdByManagedById(pmId, pageable))
        when(employeeRepository.findBy(any(Specification.class), any()))
                .thenReturn(new PageImpl<>(List.of(employee)));

        Page<GetProfileResponse> response = pmService.getAllEmployee(pmId, pageable);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(1L, response.getContent().get(0).id())
        );
    }
    @Test
    void getAllEmployeeTest_AdminIdNotFound() {
        Long pmId = 2L;
        when(employeeRepository.existsById(pmId))
                .thenReturn(false);

        assertThrows(EmployeeException.class,
                () -> pmService.getAllEmployee(pmId, pageable));
    }
    @Test
    void getAllLeaveRequest() {
        Long pmId = 2L;
        LeaveRequest leaveRequest = LeaveRequest.builder()
                .employee(Employee.builder().id(1L).build())
                .id(1L)
                .build();
        when(employeeRepository.existsById(pmId))
                .thenReturn(true);

//        when(leaveRepository.findAllByEmployeeManagedById(pmId, pageable))
        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(new PageImpl<>(List.of(leaveRequest)));

        Page<LeaveResponse> response = pmService.getAllLeaveRequest(pmId, pageable);
        assertAll(
                () -> assertEquals(1, response.getSize())
        );
    }

}
