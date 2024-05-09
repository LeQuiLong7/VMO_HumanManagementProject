package com.lql.humanresourcedemo.dto.response;

import java.util.List;

public record SearchResponse(
        GetProfileResponse employeeInfo,
        List<TechInfo> techInfo,
        List<ProjectResponse> projectInfo

) {

}
