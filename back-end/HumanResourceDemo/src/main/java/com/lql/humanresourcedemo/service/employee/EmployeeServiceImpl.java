package com.lql.humanresourcedemo.service.employee;

import com.lql.humanresourcedemo.dto.model.employee.OnLyLeaveDays;
import com.lql.humanresourcedemo.dto.model.employee.OnlyAvatar;
import com.lql.humanresourcedemo.dto.model.employee.OnlyPassword;
import com.lql.humanresourcedemo.dto.model.employee.OnlySalary;
import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.effort.EffortHistoryRecord;
import com.lql.humanresourcedemo.dto.response.employee.ChangePasswordResponse;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.leave.LeaveResponse;
import com.lql.humanresourcedemo.dto.response.project.AssignHistory;
import com.lql.humanresourcedemo.dto.response.project.ProjectDetail;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechInfo;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.file.FileException;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.exception.model.password.ChangePasswordException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.project.EmployeeProject;
import com.lql.humanresourcedemo.model.project.EmployeeProject_;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.model.tech.EmployeeTech_;
import com.lql.humanresourcedemo.repository.attendance.AttendanceRepository;
import com.lql.humanresourcedemo.repository.attendance.AttendanceSpecifications;
import com.lql.humanresourcedemo.repository.effort.EffortHistoryRepository;
import com.lql.humanresourcedemo.repository.effort.EffortHistorySpecifications;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.employee.EmployeeSpecifications;
import com.lql.humanresourcedemo.repository.leave.LeaveRepository;
import com.lql.humanresourcedemo.repository.leave.LeaveSpecifications;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectRepository;
import com.lql.humanresourcedemo.repository.project.EmployeeProjectSpecifications;
import com.lql.humanresourcedemo.repository.salary.SalaryRaiseRequestRepository;
import com.lql.humanresourcedemo.repository.tech.EmployeeTechRepository;
import com.lql.humanresourcedemo.repository.tech.EmployeeTechSpecifications;
import com.lql.humanresourcedemo.service.aws.AWSService;
import com.lql.humanresourcedemo.utility.MappingUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

import static com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus.PROCESSING;
import static com.lql.humanresourcedemo.repository.leave.LeaveSpecifications.byDate;
import static com.lql.humanresourcedemo.repository.project.EmployeeProjectSpecifications.byProjectId;
import static com.lql.humanresourcedemo.repository.salary.SalaryRaiseSpecifications.byEmployeeId;
import static com.lql.humanresourcedemo.repository.salary.SalaryRaiseSpecifications.byStatus;
import static com.lql.humanresourcedemo.utility.AWSUtility.BUCKET_NAME;
import static com.lql.humanresourcedemo.utility.FileUtility.*;
import static com.lql.humanresourcedemo.utility.MappingUtility.*;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final EmployeeTechRepository employeeTechRepository;
    private final EmployeeProjectRepository employeeProjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final LeaveRepository leaveRepository;
    private final EffortHistoryRepository effortHistoryRepository;
    private final SalaryRaiseRequestRepository salaryRepository;
    private final PasswordEncoder passwordEncoder;
    private final AWSService awsService;

    @Value("${spring.cloud.aws.region.static}")
    private String region;


    @Override
    public GetProfileResponse getProfile(Long employeeId) {
        return employeeRepository.findBy(EmployeeSpecifications.byId(employeeId), FluentQuery.FetchableFluentQuery::first)
                .map(MappingUtility::employeeToProfileResponse)
                .orElseThrow(() -> new EmployeeException(employeeId));
    }

    @Override
    public TechStackResponse getTechStack(Long employeeId) {
        requireExists(employeeId);
        return new TechStackResponse(
                employeeId,
                employeeTechRepository.findBy(EmployeeTechSpecifications.byEmployeeId(employeeId), p -> p.project(EmployeeTech_.TECH).all())
                        .stream()
                        .map(TechInfo::of)
                        .toList()
        );
    }

    @Override
    @Transactional
    public GetProfileResponse updateInfo(Long employeeId, UpdateProfileRequest request) {

        return employeeRepository.findById(employeeId)
                .map(employee -> newInfo(employee, request))
                .map(employeeRepository::save)
                .map(MappingUtility::employeeToProfileResponse)
                .orElseThrow(() -> new EmployeeException(employeeId));

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
        String fileExtension = getFileExtension(file.getOriginalFilename()).toLowerCase();
        if (!supportAvatarExtension(fileExtension)) {
            throw new FileException("Not support " + fileExtension + " file for avatar - supported types: "  + supportImageExtension.toString());
        }
        employeeRepository.findById(employeeId, OnlyAvatar.class)
                .ifPresentOrElse(avatar -> {
                        if(!avatar.avatarUrl().isBlank()) {
                            deleteOldAvatar(avatar);
                        }
                }, () -> {
                    throw new EmployeeException(employeeId);
                });

        String objectKey = String.format("image/%s.%s", employeeId, fileExtension);


        String avatarUrl = awsService.getUrlForObject(BUCKET_NAME, region, awsService.uploadFile(file, BUCKET_NAME, objectKey));
        employeeRepository.updateAvatarURLById(employeeId, avatarUrl);

        return avatarUrl;
    }
    private void deleteOldAvatar(OnlyAvatar avatar) {
        String trimmedUrl = avatar.avatarUrl().substring(8);

        // Split the remaining URL by ".s3."
        String[] parts = trimmedUrl.split("\\.s3\\.");

        // Extract bucket name and object key
        String bucketName = parts[0];
        String objectKey = parts[1].substring(parts[1].indexOf("/") + 1);

        awsService.deleteFile(bucketName, objectKey);
    }

    @Override
    @Transactional
    public LeaveResponse createLeaveRequest(Long employeeId, LeaveRequestt request) {
        if(!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException(employeeId);
        }
        if(request.type().equals(LeaveType.PAID)) {
            OnLyLeaveDays leaveDays = employeeRepository.findById(employeeId, OnLyLeaveDays.class)
                    .orElseThrow(() -> new EmployeeException(employeeId));
            if(leaveDays.leaveDays() < 1)
                throw new LeaveRequestException("Requesting a paid leave day but not enough leave day left");
        }
        if(leaveRepository.exists(LeaveSpecifications.byEmployeeId(employeeId).and(byDate(request.leaveDate())))) {
            throw new LeaveRequestException("Already have a leave request on that date");
        }
        LeaveRequest leaveRequest = toLeaveRequest(employeeRepository.getReferenceById(employeeId), request);

        return leaveRequestToResponse(leaveRepository.save(leaveRequest));
    }


    @Override
    @Transactional
    public SalaryRaiseResponse createSalaryRaiseRequest(Long employeeId, CreateSalaryRaiseRequest request) {

        if(salaryRepository.exists(byEmployeeId(employeeId).and(byStatus(PROCESSING)))) {
            throw new SalaryRaiseException("Already have a PROCESSING salary raise request");
        }
        OnlySalary currentSalary = employeeRepository.findById(employeeId, OnlySalary.class)
                .orElseThrow(() -> new EmployeeException(employeeId));

        if (request.expectedSalary() <= currentSalary.currentSalary()) {
            throw new SalaryRaiseException("Expected salary is lower than current salary");
        }
        SalaryRaiseRequest raiseRequest = toSalaryRaiseRequest(request, currentSalary.currentSalary(), employeeRepository.getReferenceById(employeeId));

        return salaryRaiseRequestToResponse(salaryRepository.save(raiseRequest));
    }

    @Override
    public Page<SalaryRaiseResponse> getAllSalaryRaiseRequest(Long employeeId, Pageable pageRequest) {
        requireExists(employeeId);
        return salaryRepository.findBy(byEmployeeId(employeeId), p -> p.sortBy(pageRequest.getSort()).page(pageRequest))
                .map(MappingUtility::salaryRaiseRequestToResponse);
    }

    @Override
    public Page<Attendance> getAllAttendanceHistory(Long employeeId,Pageable pageRequest) {
        requireExists(employeeId);
        return attendanceRepository.findBy(AttendanceSpecifications.byEmployeeId(employeeId), p -> p.sortBy(pageRequest.getSort()).page(pageRequest));
    }

    @Override
    public Page<ProjectDetail> getAllProjects(Long employeeId, Pageable pageRequest) {

        requireExists(employeeId);
        Page<EmployeeProject> projects = employeeProjectRepository.findBy(EmployeeProjectSpecifications.byEmployeeId(employeeId), p -> p.project(EmployeeProject_.PROJECT).sortBy(pageRequest.getSort()).page(pageRequest));

        return  projects
                .map(project -> new ProjectDetail(
                        project.getProject(),
                        employeeProjectRepository.findBy(byProjectId(project.getId().getProjectId()), p -> p.project(EmployeeProject_.EMPLOYEE).all())
                                .stream()
                                .map(AssignHistory::of)
                                .toList()
                ));
    }

    @Override
    public List<EffortHistoryRecord> getEffortHistory(Long employeeId, LocalDate date, boolean year) {

        requireExists(employeeId);
        LocalDate startDate = year ? date.withDayOfYear(1) : date.withDayOfMonth(1);
        if(year) {
            return effortHistoryRepository.findMonthlyAverageEffort(employeeId, startDate, date).stream().map(monthlyAverageResult -> new EffortHistoryRecord(LocalDate.of(date.getYear(), monthlyAverageResult.month(), 1), monthlyAverageResult.avgEffort())).toList();
        } else {
            return effortHistoryRepository.findBy(EffortHistorySpecifications.byEmployeeId(employeeId).and(EffortHistorySpecifications.byDateBetween(startDate, date)), p -> p.sortBy(Sort.by("id.date")).all()).stream().map(EffortHistoryRecord::of).toList();
        }
    }

    private void requireExists(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException(employeeId);
        }
    }


}
