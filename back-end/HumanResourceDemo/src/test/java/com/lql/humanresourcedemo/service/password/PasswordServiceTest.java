package com.lql.humanresourcedemo.service.password;

import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.dto.request.employee.ResetPasswordRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.resetpassword.ResetPasswordException;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.model.password.PasswordResetRequest;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.passwordreset.PasswordResetRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;

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
        when(employeeRepository.findByEmail(email, OnlyIdPersonalEmailAndFirstName.class)).thenReturn(Optional.empty());


        assertThrows(EmployeeException.class,
                () -> passwordService.createPasswordResetRequest(email),
                "Email not found");

    }

    @Test
    void createPasswordResetRequest_Success() {
        String email = "test@example.com";
//        Employee employee = new Employee();
//        employee.setPersonalEmail(email);

        when(employeeRepository.findByEmail(email, OnlyIdPersonalEmailAndFirstName.class)).thenReturn(Optional.of(Mockito.mock(OnlyIdPersonalEmailAndFirstName.class)));
        when(employeeRepository.getReferenceById(anyLong())).thenReturn(Mockito.mock(Employee.class));

        ChangePasswordResponse response = passwordService.createPasswordResetRequest(email);

        assertEquals("Success! Check your email for the token", response.message());
        verify(passwordResetRepository, times(1)).save(any(PasswordResetRequest.class));
        verify(mailService, times(1)).sendEmail(any(), anyString(), anyString());
    }

    @Test
    void testResetPassword_Success() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest(token, "newPassword", "newPassword");

        PasswordResetRequest passwordResetRequest = new PasswordResetRequest(
                        Employee.builder().id(1L).build(), token
                , LocalDateTime.now().plusHours(1)
        );

        when(passwordResetRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(passwordResetRequest));

        ChangePasswordResponse response = passwordService.resetPassword(request);

        assertEquals("Reset password successfully!", response.message());
        verify(passwordResetRepository, times(1)).delete(any(Specification.class));
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(employeeRepository, times(1)).updatePasswordById(any(), any());
    }

    @Test
    void testResetPassword_TokenIsNotFound() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest(token, "newPassword", "newPassword");

        when(passwordResetRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.empty());

        assertThrows(ResetPasswordException.class,
                () -> passwordService.resetPassword(request),
                "Token not found");

    }

    @Test
    void testResetPassword_NewPasswordAndConfirmationDoNotMatch() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest(token, "newPassword", "newPassword1");

        assertThrows(ResetPasswordException.class,
                () -> passwordService.resetPassword(request),
                "Password and confirmation password do not match");

    }


    @Test
    void testResetPassword_TokenExpired() {
        String token = UUID.randomUUID().toString();
        ResetPasswordRequest request = new ResetPasswordRequest(token, "newPassword", "newPassword");
        PasswordResetRequest passwordResetRequest = new PasswordResetRequest(
                        Employee.builder().id(1L).build(), token
                , LocalDateTime.now().minusHours(1)
        );

        when(passwordResetRepository.findBy(any(Specification.class), any()))
                .thenReturn(Optional.of(passwordResetRequest));

        assertThrows(ResetPasswordException.class,
                () -> passwordService.resetPassword(request),
                "Token expired");

    }
}