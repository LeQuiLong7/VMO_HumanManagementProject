package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.effort.EffortHistoryRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import com.lql.humanresourcedemo.service.password.PasswordService;
import com.lql.humanresourcedemo.service.project.ProjectService;
import com.lql.humanresourcedemo.service.salary.SalaryService;
import com.lql.humanresourcedemo.service.tech.TechService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ProjectService projectService;
    @Mock
    private ValidateService validateService;
    @Mock
    private AttendanceRepository attendanceRepository;
    @Mock
    private LeaveService leaveService;
    @Mock
    private EffortHistoryRepository effortHistoryRepository;
    @Mock
    private SalaryService salaryService;
    @Mock
    private TechService techService;
    @Mock
    private AWSService awsService;
    @Mock
    private PasswordService passwordService;
    private EmployeeService employeeService;


    @BeforeEach
    void setUp() {
        employeeService = new EmployeeServiceImpl(employeeRepository,projectService, validateService, attendanceRepository, leaveService, effortHistoryRepository, salaryService, techService, awsService, passwordService);
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

//    @Test
//    void getTechStack_Success() {
//        Long employeeId = 1L;
//        EmployeeTech ep = new EmployeeTech(Employee.builder().id(employeeId).build(), new Tech(1L, "a", null), 1D);
//        when(employeeRepository.existsById(employeeId)).thenReturn(true);
//        when(tec.findBy(any(Specification.class), any())).thenReturn(
//                List.of(ep)
//        );
//        TechStackResponse response = employeeService.getTechStack(employeeId);
//
//        assertAll(
//                () -> assertEquals(employeeId, response.employeeId()),
//                () -> assertEquals("a", response.techInfo().get(0).techName())
//        );
//    }
//
//    @Test
//    void changePassword_PasswordAndConfirmationDoNotMatch() {
//        Long empId = 1L;
//        ChangePasswordRequest request = new ChangePasswordRequest("old", "new", "new1");
//
//
//        assertThrows(
//                ChangePasswordException.class,
//                () -> employeeService.changePassword(empId, request),
//                "Password and confirmation password do not match"
//        );
//    }
//
//    @Test
//    void changePassword_EmployeeNotFound() {
//        Long empId = 1L;
//        ChangePasswordRequest request = new ChangePasswordRequest("", "", "");
//
//        when(employeeRepository.findById(empId, OnlyPassword.class)).thenReturn(Optional.empty());
//
//        assertThrows(
//                EmployeeException.class,
//                () -> employeeService.changePassword(empId, request),
//                "Could not find employee " + empId
//        );
//
//    }
//
//    @Test
//    void changePassword_OldPasswordNotCorrect() {
//        Long empId = 1L;
//        OnlyPassword e = new OnlyPassword("");
//        ChangePasswordRequest request = new ChangePasswordRequest("", "", "");
//        when(employeeRepository.findById(empId, OnlyPassword.class)).thenReturn(Optional.of(e));
//        when(passwordEncoder.matches(request.oldPassword(), e.password())).thenReturn(false);
//
//        assertThrows(
//                ChangePasswordException.class,
//                () -> employeeService.changePassword(empId, request),
//                "Old password is not correct"
//        );
//
//    }
//
//    @Test
//    void changePassword_Success() {
//        Long empId = 1L;
//        OnlyPassword e = new OnlyPassword("");
//        ChangePasswordRequest request = new ChangePasswordRequest("", "", "");
//        when(employeeRepository.findById(empId, OnlyPassword.class)).thenReturn(Optional.of(e));
//        when(passwordEncoder.matches(request.oldPassword(), e.password())).thenReturn(true);
//
//        assertEquals("Changed password successfully", employeeService.changePassword(empId, request).message());
//
//        verify(employeeRepository, times(1)).updatePasswordById(eq(empId), any());
//
//    }
//
//
//    @Test
//    void uploadAvatar_EmployeeNotFound() {
//        Long empId = 1L;
//        MultipartFile file = new MockMultipartFile("demo.jpg", "demo.jpg", MediaType.IMAGE_JPEG.getType(), "demo".getBytes());
//
//
//        when(employeeRepository.findById(empId, OnlyAvatar.class)).thenReturn(Optional.empty());
//
//        assertThrows(
//                EmployeeException.class,
//                () -> employeeService.uploadAvatar(empId, file),
//                "Could not find employee " + empId
//        );
//
//
//    }
//
//    @Test
//    void uploadAvatar_FileNotSupported() {
//        Long empId = 1L;
//
//        MultipartFile file = new MockMultipartFile("demo.txt", "demo.txt", MediaType.TEXT_PLAIN.getType(), "demo".getBytes());
//
//
//        assertThrows(
//                FileException.class,
//                () -> employeeService.uploadAvatar(empId, file),
//                "Not support txt file for avatar"
//        );
//    }
//
//    @Test
//    void uploadAvatar_Success() {
//        Long empId = 1L;
//        MultipartFile file = new MockMultipartFile("demo", "demo.jpg", null, "demo".getBytes());
//
//        when(employeeRepository.findById(empId, OnlyAvatar.class)).thenReturn(Optional.of(new OnlyAvatar("https://vmo-human-management-project.s3.ap-southeast-1.amazonaws.com/image/1.png")));
//        employeeService.uploadAvatar(empId, file);
//
//
//        verify(employeeRepository, times(1)).updateAvatarURLById(eq(empId), any());
//
//
//    }
//
//    @Test
//    void createSalaryRaiseRequest_Success() {
//        Long empId = 1L;
//        Employee e = Employee.builder()
//                .id(empId)
//                .build();
//        OnlySalary salary = new OnlySalary(100D);
//        CreateSalaryRaiseRequest request = new CreateSalaryRaiseRequest(200D, "");
//
//        when(employeeRepository.findById(empId, OnlySalary.class)).thenReturn(Optional.of(salary));
//        when(employeeRepository.getReferenceById(empId)).thenReturn(e);
//        when(salaryRepository.save(any(SalaryRaiseRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        SalaryRaiseResponse response = employeeService.createSalaryRaiseRequest(empId, request);
//
//
//        assertAll(
//                () -> assertEquals(SalaryRaiseRequestStatus.PROCESSING, response.status()),
//                () -> assertEquals(request.expectedSalary(), response.expectedSalary()),
//                () -> assertEquals(request.description(), response.description()),
//                () -> assertEquals(empId, response.employeeId())
//        );
//
//    }
//
//    @Test
//    void createSalaryRaiseRequest_EmployeeNotFound() {
//        Long empId = 1L;
//
//        when(employeeRepository.findById(empId, OnlySalary.class)).thenReturn(Optional.empty());
//
//        assertThrows(
//                EmployeeException.class,
//                () -> employeeService.createSalaryRaiseRequest(empId, null),
//                "Could not find employee " + empId
//        );
//    }
//
//    @Test
//    void createSalaryRaiseRequest_ExpectedSalaryLowerThanCurrentSalary() {
//        Long empId = 1L;
//        CreateSalaryRaiseRequest request = new CreateSalaryRaiseRequest(50D, "");
//        OnlySalary currentSalary = new OnlySalary(100D);
//
//
//        when(employeeRepository.findById(empId, OnlySalary.class)).thenReturn(Optional.of(currentSalary));
//
//        assertThrows(
//                SalaryRaiseException.class,
//                () -> employeeService.createSalaryRaiseRequest(empId, request),
//                "Expected salary is lower than current salary"
//        );
//    }
//
//
//    Pageable pageable = Pageable.ofSize(10);
//
//    @Test
//    void getAllAttendanceHistory() {
//        Long employeeId = 1L;
//
//
//        when(employeeRepository.existsById(employeeId)).thenReturn(true);
//        when(attendanceRepository.findBy(any(Specification.class), any()))
//                .thenReturn(new PageImpl<>(List.of(Mockito.mock(Attendance.class))));
//
//        Page<Attendance> response = employeeService.getAllAttendanceHistory(employeeId, pageable);
//        assertAll(
//                () -> assertEquals(1, response.getSize()),
//                () -> assertInstanceOf(Attendance.class, response.getContent().get(0))
//        );
//    }
//
//    @Test
//    void getAllSalaryRaiseRequest() {
//        Long employeeId = 1L;
//        Employee employee = Employee.builder()
//                .id(employeeId)
//                .build();
//
//        SalaryRaiseRequest salaryRaiseRequest = SalaryRaiseRequest.builder()
//                .id(1L)
//                .employee(employee).build();
//
//        when(employeeRepository.existsById(employeeId)).thenReturn(true);
//        when(salaryRepository.findBy(any(Specification.class), any()))
//                .thenReturn(new PageImpl<>(List.of(salaryRaiseRequest)));
//
//        Page<SalaryRaiseResponse> response = employeeService.getAllSalaryRaiseRequest(employeeId, pageable);
//        assertAll(
//                () -> assertEquals(1, response.getSize())
//        );
//    }
//
//    @Test
//    void testCreateLeaveRequest_UserNotFound() {
//        Long employeeId = 1L;
//        LeaveRequestt leaveRequestt = new LeaveRequestt(null, null, null);
//        when(employeeRepository.existsById(any(Long.class)))
//                .thenReturn(false);
//
//        assertThrows(
//                EmployeeException.class,
//                () -> employeeService.createLeaveRequest(employeeId, leaveRequestt),
//                "Could not find employee " + employeeId);
//
//
//    }
//
//    @Test
//    void testCreateLeaveRequest_PaidLeaveWithoutEnoughDays() {
//        Long employeeId = 1L;
//
//        LeaveRequestt leaveRequestt = new LeaveRequestt(null, null, LeaveType.PAID);
//        OnLyLeaveDays leaveDays = new OnLyLeaveDays(((byte) 0));
//
//        when(employeeRepository.existsById(any(Long.class)))
//                .thenReturn(true);
//        when(employeeRepository.findById(any(Long.class), eq(OnLyLeaveDays.class)))
//                .thenReturn(Optional.of(leaveDays));
//
//        assertThrows(
//                LeaveRequestException.class,
//                () -> employeeService.createLeaveRequest(employeeId, leaveRequestt),
//                "Requesting a paid leave day but not enough leave day left");
//
//    }
//
//    @Test
//    void testCreateLeaveRequest_PaidLeaveWithEnoughDays() {
//
//        Long employeeId = 1L;
//        Employee employee = Employee.builder()
//                .id(employeeId).build();
//        OnLyLeaveDays leaveDays = new OnLyLeaveDays(((byte) 5));
//
//        LeaveRequestt leaveRequestt = new LeaveRequestt(null, null, LeaveType.PAID);
//
//
//        when(employeeRepository.existsById(any(Long.class)))
//                .thenReturn(true);
//        when(employeeRepository.findById(anyLong(), eq(OnLyLeaveDays.class)))
//                .thenReturn(Optional.of(leaveDays));
//        when(employeeRepository.getReferenceById(any(Long.class)))
//                .thenReturn(employee);
//        when(leaveRepository.save(any(LeaveRequest.class)))
//                .thenAnswer(invocation -> invocation.getArgument(0));
//
//        LeaveResponse response = employeeService.createLeaveRequest(employeeId, leaveRequestt);
//
//        assertAll(
//                () -> assertEquals(leaveRequestt.leaveDate(), response.date()),
//                () -> assertEquals(employeeId, response.employeeId()),
//                () -> assertEquals(leaveRequestt.type(), response.type()),
//                () -> assertEquals(LeaveStatus.PROCESSING, response.status())
//        );
//
//    }


}