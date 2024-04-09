package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.request.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.exception.model.file.FileNotSupportException;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeNotFoundException;
import com.lql.humanresourcedemo.exception.model.password.PasswordAndConfirmationDoNotMatchException;
import com.lql.humanresourcedemo.exception.model.password.WrongOldPasswordException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.EmployeeTechRepository;
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
import java.time.LocalDateTime;
import java.util.Optional;

import static com.lql.humanresourcedemo.utility.AWSUtility.BUCKET_NAME;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeTechRepository employeeTechRepository;
    private final PasswordEncoder passwordEncoder;
    private final AWSService awsService;

    @Value("${spring.cloud.aws.region.static}")
    private String region;


    public <T> T findById(Long id, Class<T> clazz) {
        return employeeRepository.findById(id, clazz)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public <T> Optional<T> findByEmail(String email, Class<T> clazz) {
        return employeeRepository.findByEmail(email, clazz);
    }

    public TechStackResponse getTechStack(Long id) {
        return new TechStackResponse(
                employeeTechRepository.findTechInfoByEmployeeId(id)
                        .stream()
                        .map(MappingUtility::employeeTechDTOtoTechInfo)
                        .toList()
        );
    }


    @Transactional
    public GetProfileResponse updateInfo(UpdateProfileRequest request) {

        Employee updated = employeeRepository.findById(request.id())
                .map(employee -> newInfo(employee, request))
                .orElseThrow(() -> new EmployeeNotFoundException(request.id()));

        return MappingUtility.employeeToProfileResponse(employeeRepository.save(updated));
    }


    @Transactional
    public ChangePasswordResponse changePassword(ChangePasswordRequest request) {

        if(!request.oldPassword().equals(request.confirmPassword())) {
            throw new PasswordAndConfirmationDoNotMatchException(request.id());
        }

        Employee employee = employeeRepository.findById(request.id())
                .orElseThrow(() -> new EmployeeNotFoundException(request.id()));

        if(!passwordEncoder.matches(request.oldPassword(), employee.getPassword())) {
            throw new WrongOldPasswordException(request.id());
        }

        employee.setPassword(passwordEncoder.encode(request.newPassword()));
        employeeRepository.save(employee);
        return new ChangePasswordResponse("changed password successfully");

    }


    public String uploadAvatar(MultipartFile file, Long employeeId) {

        String fileExtension = FileUtility.getFileExtension(file.getOriginalFilename()).toLowerCase();

        if(!FileUtility.supportAvatarExtension(fileExtension)) {
            throw new FileNotSupportException(fileExtension);
        }

        String objectKey = String.format("image/%s.%s", employeeId, fileExtension);
        try {
            awsService.uploadFile(file, BUCKET_NAME, objectKey);
            Employee e = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException(employeeId));
            String avatarUrl = awsService.getUrlForObject(BUCKET_NAME, region, objectKey);
            e.setAvatarUrl(avatarUrl);
            employeeRepository.save(e);
            return avatarUrl;

        } catch (IOException e) {
            // TODO: handle exception
            throw new RuntimeException(e);
        }
    }




    private Employee newInfo(Employee e, UpdateProfileRequest request) {
        e.setFirstName(request.firstName());
        e.setLastName(request.lastName());
        e.setBirthDate(request.birthDate());
        e.setPhoneNumber(request.phoneNumber());
        e.setPersonalEmail(request.personalEmail());
        e.setLastUpdatedAt(LocalDateTime.now());
        return e;
    }




}
