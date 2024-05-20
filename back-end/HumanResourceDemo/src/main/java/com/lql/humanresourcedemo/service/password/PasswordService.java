package com.lql.humanresourcedemo.service.password;

import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.ResetPasswordRequest;
import com.lql.humanresourcedemo.dto.response.employee.ChangePasswordResponse;

public interface PasswordService {
    ChangePasswordResponse changePassword(Long employeeId, ChangePasswordRequest request);

    ChangePasswordResponse createPasswordResetRequest(String email);

    ChangePasswordResponse resetPassword(ResetPasswordRequest request);
}
