package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.enumeration.ProjectState;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record UpdateProjectStatusRequest (
        @NotNull Long id,
        @NotNull ProjectState newState,
        LocalDate actualStartDate,
        LocalDate actualFinishDate) {
}
