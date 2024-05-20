package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.effort.EffortHistoryRecord;
import com.lql.humanresourcedemo.dto.response.employee.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.dto.response.project.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.attendance.AttendanceSpecifications;
import com.lql.humanresourcedemo.repository.effort.EffortHistoryRepository;
import com.lql.humanresourcedemo.repository.effort.EffortHistorySpecifications;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import com.lql.humanresourcedemo.service.password.PasswordService;
import com.lql.humanresourcedemo.service.project.ProjectService;
import com.lql.humanresourcedemo.service.salary.SalaryService;
import com.lql.humanresourcedemo.service.tech.TechService;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.util.MappingUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.lql.humanresourcedemo.util.MappingUtil.newInfo;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ProjectService projectService;
    private final ValidateService validateService;
    private final AttendanceRepository attendanceRepository;
    private final LeaveService leaveService;
    private final EffortHistoryRepository effortHistoryRepository;
    private final SalaryService salaryService;
    private final TechService techService;
    private final AWSService awsService;
    private final PasswordService passwordService;


    @Override
    public GetProfileResponse getProfile(Long employeeId) {
        return employeeRepository.findById(employeeId)
                .map(MappingUtil::employeeToProfileResponse)
                .orElseThrow(() -> new EmployeeException(employeeId));
    }

    @Override
    public TechStackResponse getTechStack(Long employeeId) {
        return techService.getTechStackByEmployeeId(employeeId);
    }

    @Override
    public Page<LeaveResponse> getAllLeaveRequest(Long employeeId, Pageable pageRequest) {
        return leaveService.getAllLeaveRequest(employeeId, Role.EMPLOYEE, pageRequest);
    }

    @Override
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Long employeeId, Pageable pageRequest) {
        return salaryService.getAllSalaryRaiseRequest(employeeId, Role.EMPLOYEE, pageRequest);
    }

    @Override
    public Page<Attendance> getAllAttendanceHistory(Long employeeId,Pageable pageRequest) {
        validateService.requireExistsEmployee(employeeId);
        return attendanceRepository.findBy(AttendanceSpecifications.byEmployeeId(employeeId), p -> p.sortBy(pageRequest.getSort()).page(pageRequest));
    }

    @Override
    public Page<ProjectDetail> getAllProjects(Long employeeId, Pageable pageRequest) {

        return projectService.getAllProjectsByEmployeeId(employeeId, pageRequest);
    }

    @Override
    // year = true means get monthly average effort for the current year
    // year = false means get daily effort history for the current month
    public List<EffortHistoryRecord> getEffortHistory(Long employeeId, LocalDate date, boolean year) {
        validateService.requireExistsEmployee(employeeId);

        LocalDate startDate = year ? date.withDayOfYear(1) : date.withDayOfMonth(1);
        if(year) {
            return effortHistoryRepository.findMonthlyAverageEffort(employeeId, startDate, date).stream().map(monthlyAverageResult -> new EffortHistoryRecord(LocalDate.of(date.getYear(), monthlyAverageResult.month(), 1), monthlyAverageResult.avgEffort())).toList();
        } else {
            return effortHistoryRepository.findBy(EffortHistorySpecifications.byEmployeeId(employeeId).and(EffortHistorySpecifications.byDateBetween(startDate, date)), p -> p.sortBy(Sort.by("id.date")).all()).stream().map(EffortHistoryRecord::of).toList();
        }
    }


    @Override
    @Transactional
    public GetProfileResponse updateInfo(Long employeeId, UpdateProfileRequest request) {

        return employeeRepository.findById(employeeId)
                .map(employee -> newInfo(employee, request))
                .map(employeeRepository::save)
                .map(MappingUtil::employeeToProfileResponse)
                .orElseThrow(() -> new EmployeeException(employeeId));

    }

    @Override
    public ChangePasswordResponse changePassword(Long employeeId, ChangePasswordRequest request) {

        return passwordService.changePassword(employeeId, request);
    }

    @Override
    public String uploadAvatar(Long employeeId, MultipartFile file) {
        return awsService.uploadAvatar(employeeId, file);
    }


    @Override
    @Transactional
    public LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt request) {
        return leaveService.createLeaveRequest(employeeId, request);
    }


    @Override
    @Transactional
    public SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request) {
        return salaryService.createSalaryRaiseRequest(employeeId, request);
    }



}
