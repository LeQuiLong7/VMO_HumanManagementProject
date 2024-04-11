package com.lql.humanresourcedemo.dto.request.admin;

import java.time.LocalDate;

public record CreateNewProjectRequest(
        String name,
        String description,
        LocalDate expectedStartDate,
        LocalDate expectedFinishDate,
        Long clientId
) {
}
