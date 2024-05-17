package com.lql.humanresourcedemo.dto.request.admin;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateNewProjectRequest(

        @NotNull(message = "Project name must not be null")
        @NotBlank(message = "Project name must not be blank")
        String name,
        String description,

        @Future(message = "Expected start date must be a future date")
        @NotNull(message = "Expected start date must not be null")
        LocalDate expectedStartDate,
        @Future(message = "Expected finish date must be a future date")
        @NotNull(message = "Expected finish date must not be null")
        LocalDate expectedFinishDate
) {
}
