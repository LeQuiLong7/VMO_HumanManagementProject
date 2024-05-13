package com.lql.humanresourcedemo.dto.response;

import com.lql.humanresourcedemo.enumeration.Role;

import java.io.Serializable;

public record LoginResponse(String type, String token,  Role role) implements Serializable {
}
