package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class EmployeeServiceTest {

    private final Pageable page = Pageable.ofSize(10);
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
    void getProfile_EmployeeNotFound() {
        Long id = 1L;
        when(employeeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(
                EmployeeException.class,
                () -> employeeService.getProfile(id),
                "Could not find employee " + id
        );
        verify(employeeRepository, times(1)).findById(id);
    }

    @Test
    void getProfile_Success() {
        Long employeeId = 1L;
        when(employeeRepository.findById(employeeId))
                .thenReturn(Optional.of(Mockito.mock(Employee.class)));
        GetProfileResponse response = employeeService.getProfile(employeeId);

        assertNotNull(response);
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    @Test
    void getTechStack() {
        Long employeeId = 1L;
        employeeService.getTechStack(employeeId);

        verify(techService, times(1)).getTechStackByEmployeeId(employeeId);

    }
    @Test
    void getAllLeaveRequest() {
        Long employeeId = 1L;
        employeeService.getAllLeaveRequest(employeeId, page);

        verify(leaveService, times(1)).getAllLeaveRequest(employeeId, Role.EMPLOYEE, page);
    }
    @Test
    void getAllSalaryRaiseRequest() {
        Long employeeId = 1L;
        employeeService.getAllSalaryRaiseRequest(employeeId, page);

        verify(salaryService, times(1)).getAllSalaryRaiseRequest(employeeId, Role.EMPLOYEE, page);
    }

    @Test
    void getAllAttendanceHistory_EmployeeNotFound() {
        Long employeeId = 1L;
        doThrow(new EmployeeException(employeeId)).when(validateService).requireExistsEmployee(employeeId);
        assertThrows(
                EmployeeException.class,
                () -> employeeService.getAllAttendanceHistory(employeeId, page),
                "Could not find employee " + employeeId
        );
    }
    @Test
    void getAllAttendanceHistory_Success() {
        Long employeeId = 1L;

        when(attendanceRepository.findBy(any(Specification.class), any()))
                .thenReturn(new PageImpl<>(List.of(Mockito.mock(Attendance.class))));

        Page<Attendance> response = employeeService.getAllAttendanceHistory(employeeId, page);
        assertAll(
                () -> assertEquals(1, response.getSize()),
                () -> assertInstanceOf(Attendance.class, response.getContent().get(0))
        );
        verify(validateService, times(1)).requireExistsEmployee(employeeId);
        verify(attendanceRepository, times(1)).findBy(any(Specification.class), any());
    }

    @Test
    void getAllProject() {
        Long employeeId = 1L;
        employeeService.getAllProjects(employeeId, page);

        verify(projectService, times(1)).getAllProjectsByEmployeeId(employeeId, page);
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
        verify(employeeRepository, times(1)).findById(id);
        verify(employeeRepository, times(1)).save(any(Employee.class));

    }
    @Test
    void changePassword() {
        Long employeeId = 1L;
        ChangePasswordRequest request = new ChangePasswordRequest("", "", "");
        employeeService.changePassword(employeeId, request);

        verify(passwordService, times(1)).changePassword(employeeId, request);
    }

    @Test
    void uploadAvatar() {
        Long employeeId = 1L;
        MultipartFile file = new MockMultipartFile("fileName", "content".getBytes());

        employeeService.uploadAvatar(employeeId, file);

        verify(awsService, times(1)).uploadAvatar(employeeId, file);
    }

    @Test
    void createLeaveRequest() {
        Long employeeId = 1L;
        LeaveRequestt request = new LeaveRequestt(LocalDate.now(), "", LeaveType.PAID);

        employeeService.createLeaveRequest(employeeId, request);

        verify(leaveService, times(1)).createLeaveRequest(employeeId, request);
    }

    @Test
    void createSalaryRaiseRequest() {
        Long employeeId = 1L;
        CreateSalaryRaiseRequest request = new CreateSalaryRaiseRequest(100D, "");

        employeeService.createSalaryRaiseRequest(employeeId, request);

        verify(salaryService, times(1)).createSalaryRaiseRequest(employeeId, request);
    }

}