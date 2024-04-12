package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.model.employee.OnlyPassword;
import com.lql.humanresourcedemo.dto.model.employee.OnlySalary;
import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
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
import com.lql.humanresourcedemo.utility.FileUtility;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.lql.humanresourcedemo.utility.AWSUtility.BUCKET_NAME;
import static com.lql.humanresourcedemo.utility.MappingUtility.*;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeTechRepository employeeTechRepository;
    private final SalaryRaiseRequestRepository salaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AWSService awsService;

    @Value("${spring.cloud.aws.region.static}")
    private String region;


    public <T> T findById(Long id, Class<T> clazz) {
        return employeeRepository.findById(id, clazz)
                .orElseThrow(() -> new EmployeeException(id) {
                });
    }

    public <T> Optional<T> findByEmail(String email, Class<T> clazz) {
        return employeeRepository.findByEmail(email, clazz);
    }

    public TechStackResponse getTechStack(Long id) {
        return new TechStackResponse(
                id,
                employeeTechRepository.findTechInfoByEmployeeId(id)
                        .stream()
                        .map(MappingUtility::employeeTechDTOtoTechInfo)
                        .toList()
        );
    }


    @Transactional
    public GetProfileResponse updateInfo(Long employeeId, UpdateProfileRequest request) {

        Employee updated = employeeRepository.findById(employeeId)
                .map(employee -> newInfo(employee, request))
                .orElseThrow(() -> new EmployeeException(employeeId));

        return employeeToProfileResponse(employeeRepository.save(updated));
    }


    @Transactional
    public ChangePasswordResponse changePassword(Long employeeId, ChangePasswordRequest request) {

        if (!request.oldPassword().equals(request.confirmPassword())) {
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


    public String uploadAvatar(Long employeeId, MultipartFile file) {
        requireExists(employeeId);

        String fileExtension = FileUtility.getFileExtension(file.getOriginalFilename()).toLowerCase();

        if (!FileUtility.supportAvatarExtension(fileExtension)) {
            throw new FileException("Not support " + fileExtension + " file for avatar");
        }

        String objectKey = String.format("image/%s.%s", employeeId, fileExtension);
        try {
            awsService.uploadFile(file, BUCKET_NAME, objectKey);

            String avatarUrl = awsService.getUrlForObject(BUCKET_NAME, region, objectKey);

            employeeRepository.updateAvatarURLById(employeeId, avatarUrl);

            return avatarUrl;

        } catch (IOException e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }


    public SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request) {

        OnlySalary currentSalary = employeeRepository.findById(employeeId, OnlySalary.class)
                .orElseThrow(() -> new EmployeeException(employeeId));

        SalaryRaiseRequest salaryRaiseRequest = SalaryRaiseRequest.builder()
                .employee(employeeRepository.getReferenceById(employeeId))
                .currentSalary(currentSalary.currentSalary())
                .expectedSalary(request.expectedSalary())
                .description(request.description())
                .status(SalaryRaiseRequestStatus.PROCESSING)
                .build();

        return salaryRaiseRequestToResponse(salaryRepository.save(salaryRaiseRequest));
    }

    private void requireExists(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException(employeeId);
        }
    }
    public int updateLeaveDays() {
        return employeeRepository.increaseLeaveDaysBy1();
    }


}
