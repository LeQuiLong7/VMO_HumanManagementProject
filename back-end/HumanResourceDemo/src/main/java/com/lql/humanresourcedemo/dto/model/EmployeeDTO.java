package com.lql.humanresourcedemo.dto.model;

import com.lql.humanresourcedemo.enumeration.Role;

public record EmployeeDTO(Long id, String password, Role role) {
}
