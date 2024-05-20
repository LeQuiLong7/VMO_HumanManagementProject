package com.lql.humanresourcedemo.service.salary;

import com.lql.humanresourcedemo.dto.model.employee.OnlySalary;
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
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalaryServiceTest {

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
                () -> salaryService.getAllSalaryRaiseRequest(employeeId, Role.EMPLOYEE, Pageable.unpaged()),
                "Could not find employee " + employeeId
        );
    }

    @Test
    void handleSalaryRaiseRequest() {
    }
}