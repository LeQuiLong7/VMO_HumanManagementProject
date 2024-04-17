package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.model.employee.OnlyPassword;
import com.lql.humanresourcedemo.dto.model.employee.OnlySalary;
import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.file.FileException;
import com.lql.humanresourcedemo.exception.model.password.ChangePasswordException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.EmployeeTechRepository;
import com.lql.humanresourcedemo.repository.SalaryRaiseRequestRepository;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.utility.FileUtility;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Optional;

import static com.lql.humanresourcedemo.utility.AWSUtility.BUCKET_NAME;
import static com.lql.humanresourcedemo.utility.MappingUtility.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {


    @Mock
    private  EmployeeRepository employeeRepository;
    @Mock
    private  EmployeeTechRepository employeeTechRepository;
    @Mock
    private  SalaryRaiseRequestRepository salaryRepository;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private  AWSService awsService;
    @Mock
    private  ValidateService validateService;
    private  EmployeeService employeeService;


    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, employeeTechRepository, salaryRepository, passwordEncoder, awsService, validateService);
    }

    @Test
    void getProfile_NotFound() {
        Long id = 1L;
        when(employeeRepository.findById(id, GetProfileResponse.class)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeException.class,
                () -> employeeService.getProfile(id),
                "Could not find employee " + id
        );
    }

    @Test
    void updateInfo_EmployeeNotFound() {

        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeException.class,
                () -> employeeService.updateInfo(id, null),
                "Could not find employee " + id
        );
    }

    @Test
    void updateInfo_Success() {

        Long id = 1L;
        String newFirstName = "newFirstName";
        String newLastname = "newLastname";
        LocalDate newBirthDate = LocalDate.now();
        String newPhoneNumber = "123";
        String newPersonalEmail = "anc@gmail.com";

        UpdateProfileRequest request = new UpdateProfileRequest(newFirstName, newLastname, newBirthDate, newPhoneNumber, newPersonalEmail);


        when(employeeRepository.findById(id)).thenReturn(Optional.of(new Employee()));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));


        GetProfileResponse response = employeeService.updateInfo(id, request);

        assertAll(
                () -> assertEquals(newFirstName, response.firstName()),
                () -> assertEquals(newLastname, response.lastName()),
                () -> assertEquals(newBirthDate, response.birthDate()),
                () -> assertEquals(newPhoneNumber, response.phoneNumber()),
                () -> assertEquals(newPersonalEmail, response.personalEmail())
        );

    }

    @Test
    void getTechStack_EmployeeNotFound() {
        Long id = 1L;
        when(employeeRepository.existsById(id)).thenReturn(false);

        assertThrows(
                EmployeeException.class,
                () -> employeeService.getTechStack(id),
                "Could not find employee " + id
        );
    }

    @Test
    void changePassword_PasswordAndConfirmationDoNotMatch() {
        Long empId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("old", "new", "new1");


        assertThrows(
                ChangePasswordException.class,
                () -> employeeService.changePassword(empId, request),
                "Password and confirmation password do not match"
        );
    }

    @Test
    void changePassword_EmployeeNotFound() {
        Long empId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("", "", "");

        when(employeeRepository.findById(empId, OnlyPassword.class)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeException.class,
                () -> employeeService.changePassword(empId, request),
                "Could not find employee " + empId
        );

    }

    @Test
    void changePassword_OldPasswordNotCorrect() {
        Long empId = 1L;
        OnlyPassword e = new OnlyPassword("");
        ChangePasswordRequest request = new ChangePasswordRequest("", "", "");
        when(employeeRepository.findById(empId, OnlyPassword.class)).thenReturn(Optional.of(e));
        when(passwordEncoder.matches(request.oldPassword(), e.password())).thenReturn(false);

        assertThrows(
                ChangePasswordException.class,
                () -> employeeService.changePassword(empId, request),
                "Old password is not correct"
        );

    }

    @Test
    void changePassword_Success() {
        Long empId = 1L;
        OnlyPassword e = new OnlyPassword("");
        ChangePasswordRequest request = new ChangePasswordRequest("", "", "");
        when(employeeRepository.findById(empId, OnlyPassword.class)).thenReturn(Optional.of(e));
        when(passwordEncoder.matches(request.oldPassword(), e.password())).thenReturn(true);

        assertEquals("Changed password successfully", employeeService.changePassword(empId, request).message());

        verify(employeeRepository, times(1)).updatePasswordById(eq(empId), any());

    }


    @Test
    void uploadAvatar_EmployeeNotFound() {
        Long empId = 1L;

        when(employeeRepository.existsById(empId)).thenReturn(false);

        assertThrows(
                EmployeeException.class,
                () -> employeeService.uploadAvatar(empId, null),
                "Could not find employee " + empId
        );


    }

    @Test
    void uploadAvatar_FileNotSupported() {
        Long empId = 1L;

        when(employeeRepository.existsById(empId)).thenReturn(true);

        MultipartFile file = new MockMultipartFile("demo.txt", "demo".getBytes());

        assertThrows(
                FileException.class,
                () -> employeeService.uploadAvatar(empId, file),
                "Not support txt file for avatar"
        );
    }
    @Test
    void uploadAvatar_Success() {
        Long empId = 1L;
        MultipartFile file = new MockMultipartFile("demo", "demo.jpg", null, "demo".getBytes());

        when(employeeRepository.existsById(empId)).thenReturn(true);
        employeeService.uploadAvatar(empId, file);


        verify(employeeRepository, times(1)).updateAvatarURLById(eq(empId), any());


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

        SalaryRaiseResponse response = employeeService.createSalaryRaiseRequest(empId, request);


        assertAll(
                () -> assertEquals(SalaryRaiseRequestStatus.PROCESSING, response.status()),
                () -> assertEquals(request.expectedSalary(), response.expectedSalary()),
                () -> assertEquals(request.description(), response.description()),
                () -> assertEquals(empId, response.employeeId())
        );

    }
    @Test
    void createSalaryRaiseRequest_EmployeeNotFound() {
        Long empId = 1L;

        when(employeeRepository.findById(empId, OnlySalary.class)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeException.class,
                () -> employeeService.createSalaryRaiseRequest(empId, null),
                "Could not find employee " + empId
        );
    }


    @Test
    void getAllSalaryRaiseRequest() {
    }
}