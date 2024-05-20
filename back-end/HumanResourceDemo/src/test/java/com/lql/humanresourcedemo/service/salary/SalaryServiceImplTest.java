package com.lql.humanresourcedemo.service.salary;

import com.lql.humanresourcedemo.dto.model.employee.OnlySalary;
import com.lql.humanresourcedemo.dto.request.admin.HandleSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.salary.SalaryRaiseRequestRepository;
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
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class SalaryServiceImplTest {

    @Mock
    private SalaryRaiseRequestRepository salaryRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ValidateService validateService;
    @Mock
    private MailService mailService;
    @Mock
    private SalaryService salaryService;

    private final Pageable page = Pageable.unpaged();

    @BeforeEach
    void setUp() {
        salaryService = new SalaryServiceImpl(salaryRepository, employeeRepository, validateService, mailService);
    }

    @Test
    void createSalaryRaiseRequest_Success() {
        Long empId = 1L;
        Employee e = Employee.builder()
                .id(empId)
                .build();
        OnlySalary salary = new OnlySalary(100D);
        CreateSalaryRaiseRequest request = new CreateSalaryRaiseRequest(200D, "");

        when(employeeRepository.findById(empId, OnlySalary.class)).thenReturn(Optional.of(salary));
        when(employeeRepository.getReferenceById(empId)).thenReturn(e);
        when(salaryRepository.save(any(SalaryRaiseRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SalaryRaiseResponse response = salaryService.createSalaryRaiseRequest(empId, request);


        assertAll(
                () -> assertEquals(SalaryRaiseRequestStatus.PROCESSING, response.status()),
                () -> assertEquals(request.expectedSalary(), response.expectedSalary()),
                () -> assertEquals(request.description(), response.description()),
                () -> assertEquals(empId, response.employeeId())
        );
        verify(salaryRepository, times(1)).save(any(SalaryRaiseRequest.class));


    }

    @Test
    void createSalaryRaiseRequest_EmployeeNotFound() {
        Long empId = 1L;

        when(employeeRepository.findById(empId, OnlySalary.class)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeException.class,
                () -> salaryService.createSalaryRaiseRequest(empId, null),
                "Could not find employee " + empId
        );
    }

    @Test
    void createSalaryRaiseRequest_ExpectedSalaryLowerThanCurrentSalary() {
        Long empId = 1L;
        CreateSalaryRaiseRequest request = new CreateSalaryRaiseRequest(50D, "");
        OnlySalary currentSalary = new OnlySalary(100D);


        when(employeeRepository.findById(empId, OnlySalary.class)).thenReturn(Optional.of(currentSalary));

        assertThrows(
                SalaryRaiseException.class,
                () -> salaryService.createSalaryRaiseRequest(empId, request),
                "Expected salary is lower than current salary"
        );
    }

    @Test
    void getAllSalaryRaiseRequest_EmployeeNotFound() {
        Long employeeId = 1L;
        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        assertThrows(
                SalaryRaiseException.class,
                () -> salaryService.getAllSalaryRaiseRequest(employeeId, Role.EMPLOYEE, page),
                "Could not find employee " + employeeId
        );
    }


    @Test
    void getAllSalaryRaiseRequest_EmployeeRole() {
        Long employeeId = 1L;
        Employee employee = Employee.builder()
                .id(employeeId)
                .build();

        SalaryRaiseRequest salaryRaiseRequest = SalaryRaiseRequest.builder()
                .id(1L)
                .employee(employee).build();

        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(new PageImpl<>(List.of(salaryRaiseRequest)));

        Page<SalaryRaiseResponse> response = salaryService.getAllSalaryRaiseRequest(employeeId, Role.EMPLOYEE, page);

        assertAll(
                () -> assertEquals(1, response.getSize())
        );
        verify(salaryRepository, times(1)).findBy(any(Specification.class), any());
    }
    @Test
    void getAllSalaryRaiseRequest_PMRole() {
        Long employeeId = 1L;

        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        assertThrows(
                AccessDeniedException.class,
                () -> salaryService.getAllSalaryRaiseRequest(employeeId, Role.PM, page),
                "You are now allow to view salary raise list"
        );
    }
    @Test
    void getAllSalaryRaiseRequest_ADMINRole() {
        Long employeeId = 1L;
        Employee employee = Employee.builder()
                .id(employeeId)
                .build();

        SalaryRaiseRequest salaryRaiseRequest = SalaryRaiseRequest.builder()
                .id(1L)
                .employee(employee).build();

        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        when(salaryRepository.findAll(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(salaryRaiseRequest)));

        Page<SalaryRaiseResponse> response = salaryService.getAllSalaryRaiseRequest(employeeId, Role.ADMIN, page);

        assertAll(
                () -> assertEquals(1, response.getSize())
        );
        verify(salaryRepository, times(1)).findAll(any(Pageable.class));
    }
    @Test
    void handleSalaryRaiseRequest_RaiseRequestNotFound() {

        HandleSalaryRaiseRequest request = new HandleSalaryRaiseRequest(1L, null, null);
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.empty());


        assertThrows(
                SalaryRaiseException.class,
                () -> salaryService.handleSalaryRaiseRequest(1L, request),
                "Raise request doesn't exists"
        );
        verify(salaryRepository, times(1)).findBy(any(Specification.class), any());
    }

    @Test
    void handleSalaryRaiseRequest_RaiseRequestAlreadyHandled() {

        HandleSalaryRaiseRequest handleRequest = new HandleSalaryRaiseRequest(1L, null, null);

        SalaryRaiseRequest raiseRequest = SalaryRaiseRequest.builder()
                .status(REJECTED)
                .build();

//        when(salaryRepository.findById(handleRequest.requestId()))
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(raiseRequest));


        assertThrows(
                SalaryRaiseException.class,
                () -> salaryService.handleSalaryRaiseRequest(1L, handleRequest),
                "Raise handleRequest already handled"
        );
        verify(salaryRepository, times(1)).findBy(any(Specification.class), any());

    }

    @Test
    void handleSalaryRaiseRequest_NewStatusNotValid() {

        HandleSalaryRaiseRequest handleRequest = new HandleSalaryRaiseRequest(1L, PROCESSING, null);

        SalaryRaiseRequest raiseRequest = SalaryRaiseRequest.builder()
                .status(PROCESSING)
                .build();
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(raiseRequest));


        assertThrows(
                SalaryRaiseException.class,
                () -> salaryService.handleSalaryRaiseRequest(1L, handleRequest),
                "New status is not valid"
        );
        verify(salaryRepository, times(1)).findBy(any(Specification.class), any());

    }
    @Test
    void handleSalaryRaiseRequest_NewSalaryIsRequired() {

        HandleSalaryRaiseRequest handleRequest = new HandleSalaryRaiseRequest(1L, PARTIALLY_ACCEPTED, null);

        SalaryRaiseRequest raiseRequest = SalaryRaiseRequest.builder()
                .status(PROCESSING)
                .build();
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(raiseRequest));


        assertThrows(
                SalaryRaiseException.class,
                () -> salaryService.handleSalaryRaiseRequest(1L, handleRequest),
                "New salary is required"
        );
        verify(salaryRepository, times(1)).findBy(any(Specification.class), any());

    }
    @Test
    void handleSalaryRaiseRequest_NewSalaryIsLowerThanCurrentSalary() {

        HandleSalaryRaiseRequest handleRequest = new HandleSalaryRaiseRequest(1L, PARTIALLY_ACCEPTED, 10D);

        SalaryRaiseRequest raiseRequest = SalaryRaiseRequest.builder()
                .currentSalary(20D)
                .status(PROCESSING)
                .build();
        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(raiseRequest));


        assertThrows(
                SalaryRaiseException.class,
                () -> salaryService.handleSalaryRaiseRequest(1L, handleRequest),
                "New salary must be greater than current salary"
        );
        verify(salaryRepository, times(1)).findBy(any(Specification.class), any());

    }

    @Test
    void handleSalaryRaiseRequest_Success() {

        HandleSalaryRaiseRequest handleRequest = new HandleSalaryRaiseRequest(1L, FULLY_ACCEPTED, null);

        SalaryRaiseRequest raiseRequest = SalaryRaiseRequest.builder()
                .employee(
                        Employee.builder()
                                .id(1L)
                                .build()
                )
                .expectedSalary(200D)
                .status(PROCESSING)
                .build();

        raiseRequest.setCreatedAt(LocalDateTime.now());

        when(salaryRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(raiseRequest));

        SalaryRaiseResponse response = salaryService.handleSalaryRaiseRequest(1L, handleRequest);


        assertAll(
                () -> assertEquals(handleRequest.status(), response.status()),
                () -> assertEquals(raiseRequest.getExpectedSalary(), response.newSalary())
        );
        verify(salaryRepository, times(1)).save(raiseRequest);
        verify(employeeRepository, times(1)).updateSalaryById(anyLong(), anyDouble());
        verify(mailService, times(1)).sendEmail(any(), any(), any());
    }

}