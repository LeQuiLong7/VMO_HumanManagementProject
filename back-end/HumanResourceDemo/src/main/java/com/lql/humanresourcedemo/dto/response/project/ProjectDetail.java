package com.lql.humanresourcedemo.dto.response.project;

import com.lql.humanresourcedemo.dto.response.project.AssignHistory;
import com.lql.humanresourcedemo.model.project.Project;

import java.util.List;

public record ProjectDetail(
        Project projectInfo,

        List<AssignHistory> assignHistory


) {

}
