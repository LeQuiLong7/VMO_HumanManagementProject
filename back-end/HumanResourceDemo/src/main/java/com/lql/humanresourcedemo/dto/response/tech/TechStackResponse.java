package com.lql.humanresourcedemo.dto.response.tech;

import com.lql.humanresourcedemo.dto.response.tech.TechInfo;

import java.util.List;

public record   TechStackResponse(Long employeeId, List<TechInfo> techInfo) {

}


