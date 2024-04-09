package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.request.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.exception.model.FileNotSupportException;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeNotFoundException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.EmployeeTechRepository;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.utility.FileUtility;
import com.lql.humanresourcedemo.utility.Mapping;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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


    public <T> T findById(Long id, Class<T> clazz) {
        // TODO: create a meaningful error message
        return employeeRepository.findById(id, clazz).orElseThrow(() -> new EmployeeNotFoundException(""));
    }

    public <T> Optional<T> findByEmail(String email, Class<T> clazz) {
        return employeeRepository.findByEmail(email, clazz);
    }

    public TechStackResponse getTechStack(Long id) {
        return new TechStackResponse(employeeTechRepository.findTechInfoByEmployeeId(id).stream().map(employeeTechDTO -> new TechStackResponse.TechInfo(employeeTechDTO.getTechId(), employeeTechDTO.getTechName(), employeeTechDTO.getYearOfExperience()))
                .toList());
    }


    public GetProfileResponse updateInfo(UpdateProfileRequest request) {
        // TODO: create a meaningful error message
        Employee updated = employeeRepository.findById(request.id())
                .map(employee -> newInfo(employee, request))
                .orElseThrow(() -> new EmployeeNotFoundException(""));

        employeeRepository.save(updated);
        return Mapping.employeeToProfileResponse(updated);
    }


    @Transactional
    public ResponseEntity<ChangePasswordResponse> changePassword(ChangePasswordRequest request) {

        if(!request.oldPassword().equals(request.confirmPassword())) {
            return ResponseEntity.status(400).body(new ChangePasswordResponse("password and confirmation password do not match"));
        }

        // TODO: create a meaningful error message
        Employee employee = employeeRepository.findById(request.id())
                .orElseThrow(() -> new EmployeeNotFoundException(""));


        if(!passwordEncoder.matches(request.oldPassword(), employee.getPassword())) {
            return ResponseEntity.status(400).body(new ChangePasswordResponse("old password is not correct"));
        }

        employee.setPassword(passwordEncoder.encode(request.newPassword()));

        employeeRepository.save(employee);
        return ResponseEntity.ok().body(new ChangePasswordResponse("changed password successfully"));

    }


    public String uploadAvatar(MultipartFile file, Long employeeId) {

        String fileExtension = FileUtility.getFileExtension(file.getOriginalFilename()).toLowerCase();


        if(FileUtility.supportImageExtension.stream().noneMatch(supportType -> supportType.equals(fileExtension))) {
            throw new FileNotSupportException(fileExtension);
        }




        String objectKey = String.format("image/%s.%s", employeeId, fileExtension);
        try {
            awsService.uploadFile(file, BUCKET_NAME, objectKey);
            Employee e = employeeRepository.getReferenceById(employeeId);
            e.setAvatarUrl(objectKey);
            employeeRepository.save(e);

            return objectKey;
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
