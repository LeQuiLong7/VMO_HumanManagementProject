package com.lql.humanresourcedemo.dto.response;

import com.lql.humanresourcedemo.enumeration.Role;

import java.time.LocalDate;

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
        Role role
) {
}
