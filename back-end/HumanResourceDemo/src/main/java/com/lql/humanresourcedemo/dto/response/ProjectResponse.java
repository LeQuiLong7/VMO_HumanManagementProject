package com.lql.humanresourcedemo.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.lql.humanresourcedemo.enumeration.ProjectState;

import java.time.LocalDate;


@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProjectResponse(
        Long id,
        String name,
        String description ,
        ProjectState state,
        LocalDate expectedStartDate,
        LocalDate expectedFinishDate,
        LocalDate actualStartDate,
        LocalDate actualFinishDate,
        Long clientId
) {
}
