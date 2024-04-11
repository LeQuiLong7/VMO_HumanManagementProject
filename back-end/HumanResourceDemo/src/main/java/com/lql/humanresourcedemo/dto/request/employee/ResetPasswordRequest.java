package com.lql.humanresourcedemo.dto.request.employee;

public record ResetPasswordRequest (String token, String newPassword, String confirmPassword){
}
