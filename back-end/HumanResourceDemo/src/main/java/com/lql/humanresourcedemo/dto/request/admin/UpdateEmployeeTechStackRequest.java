package com.lql.humanresourcedemo.dto.request.admin;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;

import java.util.List;

public record UpdateEmployeeTechStackRequest (Long employeeId, List<TechStack> techStacks) {

}
