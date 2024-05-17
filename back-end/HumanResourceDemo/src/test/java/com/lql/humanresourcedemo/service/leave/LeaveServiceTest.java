package com.lql.humanresourcedemo.service.leave;

import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeaveServiceTest {
    @Mock
    private LeaveRepository leaveRepository;
    private LeaveService leaveService;

    @BeforeEach
    void setUp() {
        leaveService = new LeaveServiceImpl(leaveRepository);
    }


    @Test
    void testGetAllLeaveRequest() {
        Long employeeId = 1L;
        Employee employee = Employee.builder().id(1L).build();
        LeaveRequest leaveRequest = new LeaveRequest(1L, employee, null, null, null, null, null);

        when(leaveRepository.findBy(any(Specification.class), any()))
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
        when(leaveRepository.findBy(any(Specification.class), any()))
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


        when(leaveRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(leaveRequest));


        Optional<LeaveResponse> response = leaveService.getLeaveRequestByDateAndEmployeeId(employeeId, date);

        assertAll(
                () -> assertTrue(response.isPresent()),
                () -> assertEquals(leaveRequest.getEmployee().getId(), response.get().employeeId()),
                () -> assertEquals(leaveRequest.getDate(), response.get().date())
        );

    }
}