package com.lql.humanresourcedemo.dto.response;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record GetProfileResponse(
        Long id,
        String email,
        String firstName,
        String lastName,
        LocalDate birthDate,
        String phoneNumber,
        String avatarUrl,
        String personalEmail,
        Byte leaveDays,
        Double currentSalary,
        LocalDateTime createdAt,
        Long createdBy,
        Long managedById,
        Role role
) {
}
