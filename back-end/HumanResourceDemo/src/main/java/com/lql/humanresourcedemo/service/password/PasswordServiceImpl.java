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
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.lql.humanresourcedemo.constant.PasswordResetConstants.VALID_UNTIL_TEMPORAL_UNIT;
import static com.lql.humanresourcedemo.constant.PasswordResetConstants.VALID_UNTIL_TIME_AMOUNT;
import static com.lql.humanresourcedemo.repository.passwordreset.PasswordResetSpecifications.byEmployeeId;
import static com.lql.humanresourcedemo.repository.passwordreset.PasswordResetSpecifications.byToken;
import static com.lql.humanresourcedemo.utility.HelperUtility.buildResetMailMessage;

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

        OnlyIdPersonalEmailAndFirstName e = employeeRepository.findByEmail(email, OnlyIdPersonalEmailAndFirstName.class)
                .orElseThrow(() -> new EmployeeException("Email not found"));

        String token = UUID.randomUUID().toString();
        LocalDateTime validUntil = LocalDateTime.now().plus(VALID_UNTIL_TIME_AMOUNT, VALID_UNTIL_TEMPORAL_UNIT);

        passwordResetRepository.save(new PasswordResetRequest(employeeRepository.getReferenceById(e.id()), token, validUntil));

        mailService.sendEmail(e.personalEmail(), "[COMPANY] - RESET PASSWORD REQUEST", buildResetMailMessage(e.firstName(), email, token, validUntil));

        return new ChangePasswordResponse("Success! Check your email for the token");
    }


    @Override
    @Transactional
    public ChangePasswordResponse resetPassword(ResetPasswordRequest request) {

        if(!request.newPassword().equals(request.confirmPassword())) {
            throw new ResetPasswordException("Password and confirmation password do not match");
        }

        PasswordResetRequest passwordResetRequest = passwordResetRepository.findBy(
                    byToken(request.token()), FluentQuery.FetchableFluentQuery::first)
                .orElseThrow(() -> new ResetPasswordException("Token not found"));


        if(passwordResetRequest.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new ResetPasswordException("Token expired");
        }


        Employee e = passwordResetRequest.getEmployee();
        employeeRepository.updatePasswordById(e.getId(), passwordEncoder.encode(request.newPassword()));

        passwordResetRepository.delete(byEmployeeId(e.getId()));

        return new ChangePasswordResponse("Reset password successfully!");
    }
}
