package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import com.lql.humanresourcedemo.enumeration.Role;

import java.time.LocalDate;
import java.util.List;

public record CreateNewEmployeeRequest(
        String firstName,
        String lastName,
        LocalDate birthDate,
        String phoneNumber,
        String personalEmail,
        Double currentSalary,
        Role role,
        Long managedBy,
        List<TechStack> techStack
) {

}
