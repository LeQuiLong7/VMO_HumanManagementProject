package com.lql.humanresourcedemo.service.pm;

import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.request.pm.HandleLeaveRequest;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.leave.LeaveService;
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

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class PMServiceImplTest {
    private final Pageable page = Pageable.ofSize(10);
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private ValidateService validateService;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private LeaveService leaveService;

    private PMService pmService;

    @BeforeEach
    void setUp() {
        pmService = new PMServiceImpl(attendanceRepository, validateService, employeeRepository, leaveService);
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
    void handleLeaveRequest() {

        long pmId = 1L;
        HandleLeaveRequest handleLeaveRequest = new HandleLeaveRequest(1L, LeaveStatus.ACCEPTED);
        pmService.handleLeaveRequest(pmId, handleLeaveRequest);
        verify(leaveService, times(1)).handleLeaveRequest(pmId, handleLeaveRequest);
    }


    @Test
    void getAllEmployeeTest_Success() {
        Long pmId = 2L;
        Employee employee = Employee.builder().id(1L).build();
        when(employeeRepository.findBy(any(Specification.class), any()))
                .thenReturn(new PageImpl<>(List.of(employee)));

        Page<GetProfileResponse> response = pmService.getAllEmployee(pmId, page);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertEquals(1L, response.getContent().get(0).id())
        );
        verify(employeeRepository, times(1)).findBy(any(Specification.class), any());
        verify(validateService, times(1)).requireExistsEmployee(pmId);
    }

    @Test
    void getAllEmployee_PMIdNotFound() {
        Long pmId = 1L;
        doThrow(new EmployeeException(pmId)).when(validateService).requireExistsEmployee(pmId);

        assertThrows(
                EmployeeException.class,
                () -> pmService.getAllEmployee(pmId, page),
                "Could not find employee " + pmId
        );
        verify(validateService, times(1)).requireExistsEmployee(pmId);

    }

    @Test
    void getAllLeaveRequest() {
        long pmId = 1L;
        pmService.getAllLeaveRequest(pmId, page);
        verify(leaveService, times(1)).getAllLeaveRequest(pmId, Role.PM, page);
    }

}
