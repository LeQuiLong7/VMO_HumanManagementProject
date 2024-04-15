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
import com.lql.humanresourcedemo.service.mail.MailServiceImpl;
import com.lql.humanresourcedemo.utility.HelperUtility;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.lql.humanresourcedemo.constant.PasswordResetConstants.*;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService{

    private final PasswordResetRepository passwordResetRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;



    @Override
    @Transactional
    public ChangePasswordResponse createPasswordResetRequest(String email) {
        Employee e = employeeRepository.findByEmail(email)
                .orElseThrow(() -> new EmployeeException("Could not find employee " + email));

        String token = UUID.randomUUID().toString();
        LocalDateTime validUntil = LocalDateTime.now().plus(VALID_UNTIL_TIME_AMOUNT, VALID_UNTIL_TEMPORAL_UNIT);

        PasswordResetRequest passwordResetRequest = new PasswordResetRequest(new PasswordResetRequest.PasswordResetRequestId(e, token), validUntil);

        passwordResetRepository.save(passwordResetRequest);

        mailService.sendEmail(e.getPersonalEmail(), "[COMPANY] - RESET PASSWORD REQUEST", HelperUtility.buildResetMailMessage(e.getFirstName(), email, token, validUntil));

        return new ChangePasswordResponse("Success! Check your email for the token");
    }


    @Override
    @Transactional
    public ChangePasswordResponse resetPassword(ResetPasswordRequest request) {
        PasswordResetRequest passwordResetRequest = passwordResetRepository.findByToken(request.token())
                .orElseThrow(() -> new ResetPasswordException("token is not valid"));

        if(!request.newPassword().equals(request.confirmPassword())) {
            throw new ResetPasswordException("Password and confirmation password do not match");
        }

        if(passwordResetRequest.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new ResetPasswordException("token expired");
        }


        Employee e = passwordResetRequest.getId().getEmployee();
        e.setPassword(passwordEncoder.encode(request.newPassword()));
        employeeRepository.save(e);

        passwordResetRepository.deleteByEmployeeId(e.getId());


        return new ChangePasswordResponse("Reset password successfully!");
    }
}
