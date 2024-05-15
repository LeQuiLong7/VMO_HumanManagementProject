package com.lql.humanresourcedemo.dto.response.search;

import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.project.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechInfo;

import java.util.List;

public record SearchResponse(
        GetProfileResponse employeeInfo,
        List<TechInfo> techInfo,
        List<ProjectResponse> projectInfo

) {

}
