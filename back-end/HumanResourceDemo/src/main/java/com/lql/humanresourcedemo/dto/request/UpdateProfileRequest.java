package com.lql.humanresourcedemo.dto.request;

import java.time.LocalDate;

public record UpdateProfileRequest(Long id,
                                            String firstName,
                                            String lastName,
                                            LocalDate birthDate,
                                            String phoneNumber,
                                            String personalEmail) {
}
