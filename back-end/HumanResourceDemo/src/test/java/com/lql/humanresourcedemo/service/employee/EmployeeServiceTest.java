package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.model.employee.OnlyPassword;
import com.lql.humanresourcedemo.dto.model.employee.OnlySalary;
import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.file.FileException;
import com.lql.humanresourcedemo.exception.model.password.ChangePasswordException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectRepository;
import com.lql.humanresourcedemo.repository.salary.SalaryRaiseRequestRepository;
import com.lql.humanresourcedemo.repository.tech.EmployeeTechRepository;
import com.lql.humanresourcedemo.service.aws.AWSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {


    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private EmployeeTechRepository employeeTechRepository;
    @Mock
    private EmployeeProjectRepository employeeProjectRepository;
    @Mock
    private SalaryRaiseRequestRepository salaryRepository;
    @Mock
    private  PasswordEncoder passwordEncoder;
    @Mock
    private  AWSService awsService;

    @Mock
    private AttendanceRepository attendanceRepository;
    private  EmployeeService employeeService;


    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository, employeeTechRepository, employeeProjectRepository, attendanceRepository, salaryRepository, passwordEncoder, awsService);
    }

    @Test
    void getProfile_NotFound() {
        Long id = 1L;
        when(employeeRepository.findBy(any(Specification.class), any())).thenReturn(Optional.empty());

        assertThrows(
                EmployeeException.class,
                () -> employeeService.getProfile(id),
                "Could not find employee " + id
        );
    }

    @Test
    void getProfile_Success() {
        Long employeeId = 1L;

//        GetProfileResponse profileResponse = new GetProfileResponse(employeeId, "", "", "", null, null, null, null, null, null, null, null, null, null, NULL);
        when(employeeRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(Mockito.mock(Employee.class)));
        GetProfileResponse response = employeeService.getProfile(employeeId);

//        assertEquals(employeeId, response.id());

        assertNotNull(response);
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
    void getTechStack_Success() {
        Long employeeId = 1L;
        EmployeeTech ep = new EmployeeTech(Employee.builder().id(employeeId).build(), new Tech(1L, "a", null), 1D);
        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        when(employeeTechRepository.findBy(any(Specification.class), any())).thenReturn(
                List.of(ep)
        );
        TechStackResponse response = employeeService.getTechStack(employeeId);

        assertAll(
                () -> assertEquals(employeeId, response.employeeId()),
                () -> assertEquals("a", response.techInfo().get(0).techName())
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
        MultipartFile file = new MockMultipartFile("demo.jpg", "demo.jpg", MediaType.IMAGE_JPEG.getType(),  "demo".getBytes());


        when(employeeRepository.existsById(empId)).thenReturn(false);

        assertThrows(
                EmployeeException.class,
                () -> employeeService.uploadAvatar(empId, file),
                "Could not find employee " + empId
        );


    }

    @Test
    void uploadAvatar_FileNotSupported() {
        Long empId = 1L;

        MultipartFile file = new MockMultipartFile("demo.txt", "demo.txt", MediaType.TEXT_PLAIN.getType(),  "demo".getBytes());


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
    void createSalaryRaiseRequest_ExpectedSalaryLowerThanCurrentSalary() {
        Long empId = 1L;
        CreateSalaryRaiseRequest request = new CreateSalaryRaiseRequest(50D, "");
        OnlySalary currentSalary = new OnlySalary(100D);


        when(employeeRepository.findById(empId, OnlySalary.class)).thenReturn(Optional.of(currentSalary));

        assertThrows(
                SalaryRaiseException.class,
                () -> employeeService.createSalaryRaiseRequest(empId, request),
                "Expected salary is lower than current salary"
        );
    }



    Pageable pageable = Pageable.ofSize(10);
    @Test
    public void getAllAttendanceHistory() {
        Long employeeId = 1L;


        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        when(attendanceRepository.findBy(any(Specification.class), any()))
                .thenReturn(new PageImpl<>(List.of(Mockito.mock(Attendance.class))));

        Page<Attendance> response = employeeService.getAllAttendanceHistory(employeeId, pageable);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertInstanceOf(Attendance.class, response.getContent().get(0))
        );
    }
    @Test
    public void getAllSalaryRaiseRequest() {
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

        Page<SalaryRaiseResponse> response = employeeService.getAllSalaryRaiseRequest(employeeId, pageable);
        assertAll(
                () -> assertEquals(1, response.getSize())
        );
    }

//    @Test
//    public void getAllProjects() {
//        Long employeeId = 1L;
//        Project project = Project.builder()
//                .id(1L)
//                .build();
//        Employee employee = Employee.builder()
//                .id(employeeId)
//                .build();
//
//        EmployeeProject ep = new EmployeeProject(
//                        employee,
//                        project,
//                      10
//        );
//
//        employee.setProjects(List.of(ep));
//
//        when(employeeRepository.existsById(employeeId)).thenReturn(true);
//
//        when(employeeProjectRepository.findBy(any(Specification.class), any()))
//                .thenReturn(new PageImpl<>(List.of(ep)));
////        when(employeeProjectRepository.findBy(any(Specification.class), any()))
////                .thenReturn(List.of(ep));
//
//        Page<ProjectDetail> response = employeeService.getAllProjects(employeeId, pageable);
//        assertAll(
//                () -> assertEquals(1, response.getSize())
//        );
//    }


}