package com.lql.humanresourcedemo.service.password;

import com.lql.humanresourcedemo.dto.request.employee.ResetPasswordRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.resetpassword.ResetPasswordException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.password.PasswordResetRequest;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import com.lql.humanresourcedemo.repository.PasswordResetRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.testcontainers.shaded.org.bouncycastle.openssl.PasswordException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordServiceTest {
    @Mock
    private PasswordResetRepository passwordResetRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private MailService mailService;

    private PasswordService passwordService;

    @BeforeEach
    void setUp() {
        passwordService = new PasswordServiceImpl(passwordResetRepository, employeeRepository, passwordEncoder, mailService);

    }

    @Test
    void createPasswordResetRequest_CouldNotFoundEmployee() {
        String email = "test@example.com";
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.empty());


        EmployeeException exception = assertThrows(EmployeeException.class, () -> {
            passwordService.createPasswordResetRequest(email);
        });

        assertEquals("Could not find employee " + email, exception.getMessage());
    }

    @Test
    void createPasswordResetRequest_Success() {
        String email = "test@example.com";
        Employee employee = new Employee();
        employee.setPersonalEmail(email);

        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));

        ChangePasswordResponse response = passwordService.createPasswordResetRequest(email);

        assertEquals("Success! Check your email for the token", response.message());
        verify(passwordResetRepository, times(1)).save(any(PasswordResetRequest.class));
        verify(mailService, times(1)).sendEmail(eq(email), anyString(), anyString());
    }

    @Test
    void testResetPassword_Success() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest(token, "newPassword", "newPassword");

        PasswordResetRequest passwordResetRequest = new PasswordResetRequest(
                new PasswordResetRequest.PasswordResetRequestId(
                        Employee.builder().id(1L).build(), token
                ), LocalDateTime.now().plusHours(1)
        );

        when(passwordResetRepository.findByToken(token))
                .thenReturn(Optional.of(passwordResetRequest));

        ChangePasswordResponse response = passwordService.resetPassword(request);

        assertEquals("Reset password successfully!", response.message());
        verify(passwordResetRepository, times(1)).findByToken(eq(token));
        verify(passwordResetRepository, times(1)).deleteByEmployeeId(anyLong());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    void testResetPassword_TokenIsNotFound() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest(token, "newPassword", "newPassword");

        when(passwordResetRepository.findByToken(token))
                .thenReturn(Optional.empty());

        ResetPasswordException exception = assertThrows(ResetPasswordException.class, () -> {
            passwordService.resetPassword(request);
        });

        assertEquals("token is not found", exception.getMessage());
    }
    @Test
    void testResetPassword_NewPasswordAndConfirmationDoNotMatch() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest(token, "newPassword", "newPassword1");

        ResetPasswordException exception = assertThrows(ResetPasswordException.class, () -> {
            passwordService.resetPassword(request);
        });

        assertEquals("Password and confirmation password do not match", exception.getMessage());
    }



    @Test
    void testResetPassword_TokenExpired() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest(token, "newPassword", "newPassword");
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest(
                new PasswordResetRequest.PasswordResetRequestId(
                        Employee.builder().id(1L).build(), token
                ), LocalDateTime.now().minusHours(1)
        );

        when(passwordResetRepository.findByToken(token))
                .thenReturn(Optional.of(passwordResetRequest));

        ResetPasswordException exception = assertThrows(ResetPasswordException.class, () -> {
            passwordService.resetPassword(request);
        });

        assertEquals("token expired", exception.getMessage());
    }
}