package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.enumeration.ProjectState;

import java.time.LocalDate;

public record UpdateProjectStatusRequest (Long id, ProjectState newState, LocalDate actualStartDate, LocalDate actualFinishDate) {
}
