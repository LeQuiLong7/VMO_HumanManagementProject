package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.model.employee.OnlyPassword;
import com.lql.humanresourcedemo.dto.model.employee.OnlySalary;
import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.file.FileException;
import com.lql.humanresourcedemo.exception.model.password.ChangePasswordException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.repository.*;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.utility.FileUtility;
import com.lql.humanresourcedemo.utility.HelperUtility;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.lql.humanresourcedemo.utility.AWSUtility.BUCKET_NAME;
import static com.lql.humanresourcedemo.utility.HelperUtility.validateAndBuildPageRequest;
import static com.lql.humanresourcedemo.utility.MappingUtility.*;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeTechRepository employeeTechRepository;
    private final EmployeeProjectRepository employeeProjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final SalaryRaiseRequestRepository salaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AWSService awsService;

    @Value("${spring.cloud.aws.region.static}")
    private String region;


    @Override
    public GetProfileResponse getProfile(Long employeeId) {
        return employeeRepository.findById(employeeId, GetProfileResponse.class)
                .orElseThrow(() -> new EmployeeException(employeeId));
    }

    @Override
    public TechStackResponse getTechStack(Long id) {
        requireExists(id);
        return new TechStackResponse(
                id,
                employeeTechRepository.findTechInfoByEmployeeId(id)
        );
    }

    @Override
    @Transactional
    public GetProfileResponse updateInfo(Long employeeId, UpdateProfileRequest request) {

        Employee updated = employeeRepository.findById(employeeId)
                .map(employee -> newInfo(employee, request))
                .orElseThrow(() -> new EmployeeException(employeeId));

        return employeeToProfileResponse(employeeRepository.save(updated));
    }

    @Override
    @Transactional
    public ChangePasswordResponse changePassword(Long employeeId, ChangePasswordRequest request) {

        if (!request.newPassword().equals(request.confirmPassword())) {
            throw new ChangePasswordException("Password and confirmation password do not match");
        }

        OnlyPassword p = employeeRepository.findById(employeeId, OnlyPassword.class)
                .orElseThrow(() -> new EmployeeException(employeeId));

        if (!passwordEncoder.matches(request.oldPassword(), p.password())) {
            throw new ChangePasswordException("Old password is not correct");
        }

        employeeRepository.updatePasswordById(employeeId, passwordEncoder.encode(request.newPassword()));
        return new ChangePasswordResponse("Changed password successfully");

    }

    @Override
    @Transactional
    public String uploadAvatar(Long employeeId, MultipartFile file) {

        String fileExtension = FileUtility.getFileExtension(file.getOriginalFilename()).toLowerCase();

        if (!FileUtility.supportAvatarExtension(fileExtension)) {
            throw new FileException("Not support " + fileExtension + " file for avatar - supported types: "  + FileUtility.supportImageExtension.toString());
        }
        requireExists(employeeId);

        String objectKey = String.format("image/%s.%s", employeeId, fileExtension);
        awsService.uploadFile(file, BUCKET_NAME, objectKey);

        String avatarUrl = awsService.getUrlForObject(BUCKET_NAME, region, objectKey);

        employeeRepository.updateAvatarURLById(employeeId, avatarUrl);

        return avatarUrl;
    }

    @Override
    @Transactional
    public SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request) {

        OnlySalary currentSalary = employeeRepository.findById(employeeId, OnlySalary.class)
                .orElseThrow(() -> new EmployeeException(employeeId));
        if (request.expectedSalary() <= currentSalary.currentSalary()) {
            throw new SalaryRaiseException("Expected salary is lower than current salary");
        }
        SalaryRaiseRequest salaryRaiseRequest = SalaryRaiseRequest.builder()
                .employee(employeeRepository.getReferenceById(employeeId))
                .currentSalary(currentSalary.currentSalary())
                .expectedSalary(request.expectedSalary())
                .description(request.description())
                .status(SalaryRaiseRequestStatus.PROCESSING)
                .build();

        return salaryRaiseRequestToResponse(salaryRepository.save(salaryRaiseRequest));
    }

    @Override
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Long employeeId, Pageable pageRequest) {
        requireExists(employeeId);
        return salaryRepository.findAllByEmployeeId(employeeId, pageRequest).map(MappingUtility::salaryRaiseRequestToResponse);
    }

    @Override
    public Page<Attendance> getAllAttendanceHistory(Long employeeId,Pageable pageRequest) {
        requireExists(employeeId);
        return attendanceRepository.findAllByEmployeeId(employeeId, pageRequest);
    }

    @Override
    public Page<ProjectDetail> getAllProjects(Long employeeId, Pageable pageRequest) {
        requireExists(employeeId);

        return employeeProjectRepository.findAllByIdEmployeeId(employeeId, pageRequest)
                .map(employeeProject ->
                        new ProjectDetail(employeeProject.getId().getProject(),
                                employeeProjectRepository.getAssignHistoryByProjectId(employeeProject.getId().getProject().getId())
                        ));
    }

    private void requireExists(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException(employeeId);
        }
    }


}
