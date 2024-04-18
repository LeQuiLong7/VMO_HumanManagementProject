package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record UpdateProfileRequest(
                                            String firstName,
                                            String lastName,
                                            @Past
                                            LocalDate birthDate,

                                            String phoneNumber,
                                            @Email
                                            String personalEmail) {
}
