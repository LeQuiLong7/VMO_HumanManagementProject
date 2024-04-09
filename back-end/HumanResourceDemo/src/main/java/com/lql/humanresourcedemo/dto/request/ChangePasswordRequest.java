package com.lql.humanresourcedemo.dto.request;

public record ChangePasswordRequest(Long id, String oldPassword, String confirmPassword, String newPassword) {
}
