package com.lql.humanresourcedemo.utility;

import com.lql.humanresourcedemo.dto.model.EmployeeTechDTO;
import com.lql.humanresourcedemo.dto.request.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewEmployeeRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.SalaryRaiseResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;

import java.time.LocalDateTime;

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
                e.getRole()
        );
    }

    public static TechStackResponse.TechInfo employeeTechDTOtoTechInfo(EmployeeTechDTO employeeTechDTO) {
        return new TechStackResponse.TechInfo(
                employeeTechDTO.getTechId(),
                employeeTechDTO.getTechName(),
                employeeTechDTO.getYearOfExperience());
    }

    public static Employee newInfo(Employee e, UpdateProfileRequest request) {
        e.setFirstName(request.firstName());
        e.setLastName(request.lastName());
        e.setBirthDate(request.birthDate());
        e.setPhoneNumber(request.phoneNumber());
        e.setPersonalEmail(request.personalEmail());
        e.setLastUpdatedAt(LocalDateTime.now());
        return e;
    }


    public static Employee createNewEmployeeRequestToEmployee(CreateNewEmployeeRequest request) {

        return Employee.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .birthDate(request.birthDate())
                .phoneNumber(request.phoneNumber())
                .personalEmail(request.personalEmail())
                .currentSalary(request.currentSalary())
                .role(request.role())
                .lastUpdatedAt(LocalDateTime.now())
                .build();
    }

    public static SalaryRaiseResponse salaryRaiseRequestToResponse(SalaryRaiseRequest request) {
        return new SalaryRaiseResponse(
                request.getEmployee().getId(),
                request.getExpectedSalary(),
                request.getDescription(),
                request.getStatus(),
                request.getCreatedAt(),
                request.getCreatedBy(),
                request.getNewSalary(),
                request.getApprovedBy() != null ? request.getApprovedBy().getId() : null);
    }
}
