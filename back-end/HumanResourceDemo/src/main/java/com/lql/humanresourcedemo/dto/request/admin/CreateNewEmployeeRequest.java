package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import com.lql.humanresourcedemo.enumeration.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;
import java.util.List;

public record CreateNewEmployeeRequest(
        String firstName,
        String lastName,
        @Past
        LocalDate birthDate,
        String phoneNumber,
        @Email
        String personalEmail,
        @Min(0)
        Double currentSalary,
        Role role,
        Long managedBy,
        List<TechStack> techStack
) {

}
