package com.lql.humanresourcedemo.dto.request.admin;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record CreateNewProjectRequest(

        @NotNull
        @NotBlank
        String name,
        String description,

        @Future
        @NotNull
        LocalDate expectedStartDate,
        @Future
        @NotNull
        LocalDate expectedFinishDate
) {
}
