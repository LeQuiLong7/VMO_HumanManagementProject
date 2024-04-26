package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    GetProfileResponse getProfile(Long employeeId);
    GetProfileResponse updateInfo(Long employeeId, UpdateProfileRequest request);

    TechStackResponse getTechStack(Long id);

    ChangePasswordResponse changePassword(Long employeeId, ChangePasswordRequest request);

    String uploadAvatar(Long employeeId, MultipartFile file);

    SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request);

    Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Long employeeId, String page, String pageSize, List<String> properties, List<String> orders);
    Page<Attendance> getAllAttendanceHistory(Long employeeId, String page, String pageSize, List<String> properties, List<String> orders);

    Page<ProjectDetail> getAllProjects(Long employeeId, String page, String pageSize, List<String> properties, List<String> orders);
}
