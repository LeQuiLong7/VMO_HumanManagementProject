package com.lql.humanresourcedemo.dto.response;

import com.lql.humanresourcedemo.enumeration.Role;

public record LoginResponse(String type, String token, Role role) {
}
