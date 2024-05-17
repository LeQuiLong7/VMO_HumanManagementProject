package com.lql.humanresourcedemo.dto.request.employee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UpdateProfileRequest(
                                            String firstName,
                                            String lastName,
                                            @Past(message = "Birth date must be a past date")
                                            LocalDate birthDate,

                                            String phoneNumber,
                                            @Email(message = "Email is not valid")
                                            String personalEmail) {
}
