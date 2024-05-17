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
import com.lql.humanresourcedemo.model.attendance.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeService {

    GetProfileResponse getProfile(Long employeeId);
    TechStackResponse getTechStack(Long employeeId);
    Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Long employeeId, Pageable pageRequest);
    Page<Attendance> getAllAttendanceHistory(Long employeeId, Pageable pageRequest);
    Page<ProjectDetail> getAllProjects(Long employeeId, Pageable pageRequest);

    // year = false means get everyday effort history this month
    // year = true means get monthly average effort history this year
    List<EffortHistoryRecord> getEffortHistory(Long employeeId, LocalDate date, boolean year);

    GetProfileResponse updateInfo(Long employeeId, UpdateProfileRequest request);
    ChangePasswordResponse changePassword(Long employeeId, ChangePasswordRequest request);
    String uploadAvatar(Long employeeId, MultipartFile file);
    LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt leaveRequestt);
    SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request);
}
