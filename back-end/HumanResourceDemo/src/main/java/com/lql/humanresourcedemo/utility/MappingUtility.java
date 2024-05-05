package com.lql.humanresourcedemo.utility;

import com.lql.humanresourcedemo.dto.request.admin.CreateNewProjectRequest;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewEmployeeRequest;
import com.lql.humanresourcedemo.dto.request.pm.CheckAttendanceRequest;
import com.lql.humanresourcedemo.dto.response.*;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.model.attendance.Attendance;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.project.Project;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static com.lql.humanresourcedemo.enumeration.ProjectState.INITIATION;

public class MappingUtility {

    public static GetProfileResponse employeeToProfileResponse(Employee e) {
        return new GetProfileResponse(
                e.getId(),
                e.getEmail(),
                e.getFirstName(),
                e.getLastName(),
                e.getBirthDate(),
                e.getPhoneNumber(),
                e.getAvatarUrl(),
                e.getPersonalEmail(),
                e.getLeaveDays(),
                e.getCurrentSalary(),
                e.getCreatedAt(),
                e.getCreatedBy(),
                e.getManagedBy() != null ? e.getManagedBy().getId() : null,
                e.getRole()
        );
    }

//    public static TechStackResponse.TechInfo employeeTechDTOtoTechInfo(EmployeeTechDTO employeeTechDTO) {
//        return new TechStackResponse.TechInfo(
//                employeeTechDTO.getTechId(),
//                employeeTechDTO.getTechName(),
//                employeeTechDTO.getYearOfExperience());
//    }

    public static Employee newInfo(Employee e, UpdateProfileRequest request) {
        e.setFirstName(request.firstName());
        e.setLastName(request.lastName());
        e.setBirthDate(request.birthDate());
        e.setPhoneNumber(request.phoneNumber());
        e.setPersonalEmail(request.personalEmail());
        e.setLastUpdatedAt(LocalDateTime.now());
        return e;
    }


    public static Employee toEmployee(CreateNewEmployeeRequest request) {

        return Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .phoneNumber(request.phoneNumber())
                .personalEmail(request.personalEmail())
                .currentSalary(request.currentSalary())
                .role(request.role())
                .leaveDays((byte) 0)
                .quit(false)
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }
    public static Attendance toAttendance(CheckAttendanceRequest.AttendanceDetail attendanceDetail, LocalDate date, Employee e) {
        return Attendance.builder()
                .employee(e)
                .date(date)
                .timeIn(attendanceDetail.timeIn())
                .timeOut(attendanceDetail.timeOut())
                .build();
    }

    public static LeaveRequest toLeaveRequest(Employee e, LeaveRequestt request) {
        return LeaveRequest.builder()
                .employee(e)
                .date(request.leaveDate())
                .type(request.type())
                .status(LeaveStatus.PROCESSING)
                .reason(request.reason())
                .build();
    }
    public static Project toProject(CreateNewProjectRequest request) {
        return Project.builder()
                .name(request.name())
                .description(request.description())
                .expectedStartDate(request.expectedStartDate())
                .expectedFinishDate(request.expectedFinishDate())
                .state(INITIATION)
//                .client(clientRepository.getReferenceById(request.clientId()))
                .build();
    }
    public static SalaryRaiseRequest toSalaryRaiseRequest(CreateSalaryRaiseRequest request, Double currentSalary) {
        return  SalaryRaiseRequest.builder()
                .currentSalary(currentSalary)
                .expectedSalary(request.expectedSalary())
                .description(request.description())
                .status(SalaryRaiseRequestStatus.PROCESSING)
                .build();
    }


    public static SalaryRaiseResponse salaryRaiseRequestToResponse(SalaryRaiseRequest request) {
        return new SalaryRaiseResponse(
                request.getId(),
                request.getEmployee().getId(),
                request.getEmployee().getLastName() + ' ' + request.getEmployee().getFirstName(),
                request.getEmployee().getAvatarUrl(),
                request.getCurrentSalary(),
                request.getExpectedSalary(),
                request.getDescription(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getCreatedBy(),
                request.getNewSalary(),
                request.getApprovedBy() != null ? request.getApprovedBy().getId() : null);
    }

    public static LeaveResponse leaveRequestToResponse(LeaveRequest request) {
        return new LeaveResponse(request.getId(),
                request.getEmployee().getId(),
                request.getEmployee().getLastName() + " " + request.getEmployee().getFirstName(),
                request.getEmployee().getAvatarUrl(),
                request.getDate(),
                request.getCreatedAt(),
                request.getType(),
                request.getReason(),
                request.getStatus(),
                request.getApprovedBy() == null ? null : request.getApprovedBy().getId());
    }


    public static ProjectResponse projectToProjectResponse(Project p) {
        return new ProjectResponse(p.getId(), p.getName(), p.getDescription(), p.getState(), p.getExpectedStartDate(), p.getExpectedFinishDate(), p.getActualStartDate(), p.getActualFinishDate());
    }
}
