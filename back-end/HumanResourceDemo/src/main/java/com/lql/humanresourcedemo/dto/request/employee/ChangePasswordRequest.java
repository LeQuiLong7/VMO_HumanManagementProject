package com.lql.humanresourcedemo.dto.request.employee;

public record ChangePasswordRequest(Long id, String oldPassword, String confirmPassword, String newPassword) {
}
