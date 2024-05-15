package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.enumeration.ProjectState;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateProjectStatusRequest (
        @NotNull (message = "Project id must not be null")
        Long id,
        @NotNull (message = "New state must not be null")
        ProjectState newState,
        LocalDate actualStartDate,
        LocalDate actualFinishDate) {
}
