package com.lql.humanresourcedemo.dto.model.employee;

import com.lql.humanresourcedemo.enumeration.Role;

public record OnlyIdPasswordAndRole(Long id, String password, Role role) {
}
