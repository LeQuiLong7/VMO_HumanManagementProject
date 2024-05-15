package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.employee.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.project.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface EmployeeService {

    GetProfileResponse getProfile(Long employeeId);
    GetProfileResponse updateInfo(Long employeeId, UpdateProfileRequest request);
    TechStackResponse getTechStack(Long id);
    ChangePasswordResponse changePassword(Long employeeId, ChangePasswordRequest request);
    String uploadAvatar(Long employeeId, MultipartFile file);
    SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request);
    Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Long employeeId, Pageable pageRequest);
    Page<Attendance> getAllAttendanceHistory(Long employeeId, Pageable pageRequest);
    Page<ProjectDetail> getAllProjects(Long employeeId, Pageable pageRequest);
}
