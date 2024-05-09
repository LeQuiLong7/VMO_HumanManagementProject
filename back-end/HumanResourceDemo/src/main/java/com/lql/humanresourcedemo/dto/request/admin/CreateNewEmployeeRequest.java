package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import com.lql.humanresourcedemo.enumeration.Role;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.util.List;

public record CreateNewEmployeeRequest(

        @NotNull(message = "First name must not be null")
        @NotBlank(message = "First name must not be blank")
        String firstName,
        @NotNull(message = "Last name must not be null")
        @NotBlank(message = "Last name must not be blank")
        String lastName,
        @Past(message = "Birth date is not valid")
        @NotNull(message = "Birth date must not be null")
        LocalDate birthDate,
        @NotNull(message = "Phone number must not be null")
        @NotBlank(message = "Phone number must not be blank")
        String phoneNumber,
        @Email(message = "Email is not a valid email address")
        @NotNull(message = "Email must not be null")
        String personalEmail,
        @Min(value = 0, message = "Salary is not valid")
        Double currentSalary,
        @NotNull(message = "Role must not be null")

        Role role,
        @NotNull(message = "Manged by must not be null")
        Long managedBy,
        List<TechStack> techStack
) {

}
