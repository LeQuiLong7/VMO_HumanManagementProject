package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.Email;

import java.time.LocalDate;

public record UpdateProfileRequest(
                                            String firstName,
                                            String lastName,
                                            LocalDate birthDate,
                                            String phoneNumber,
                                            @Email
                                            String personalEmail) {
}
