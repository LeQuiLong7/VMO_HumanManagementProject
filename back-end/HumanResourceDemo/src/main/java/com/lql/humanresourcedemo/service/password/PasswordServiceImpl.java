package com.lql.humanresourcedemo.service.password;


import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPersonalEmailAndFirstName;
import com.lql.humanresourcedemo.dto.request.employee.ResetPasswordRequest;
import com.lql.humanresourcedemo.dto.response.employee.ChangePasswordResponse;
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
    // create a password reset request, a uuid token will be sent to the personal email of the account
    public ChangePasswordResponse createPasswordResetRequest(String email) {
        // check if exists any account corresponding to the request email
        OnlyIdPersonalEmailAndFirstName e = employeeRepository.findByEmail(email, OnlyIdPersonalEmailAndFirstName.class)
                .orElseThrow(() -> new EmployeeException("Email not found"));

        // create a uuid token and set the valid until time
        String token = UUID.randomUUID().toString();
        LocalDateTime validUntil = LocalDateTime.now().plus(VALID_UNTIL_TIME_AMOUNT, VALID_UNTIL_TEMPORAL_UNIT);
        // save the reset password request to the database
        passwordResetRepository.save(new PasswordResetRequest(employeeRepository.getReferenceById(e.id()), token, validUntil));

        // send the token to the personal email of the account
        mailService.sendEmail(e.personalEmail(), "[COMPANY] - RESET PASSWORD REQUEST", buildResetMailMessage(e.firstName(), email, token, validUntil));

        return new ChangePasswordResponse("Success! Check your email for the token");
    }


    @Override
    @Transactional
    public ChangePasswordResponse resetPassword(ResetPasswordRequest request) {

        // new password and confirm password must match
        if(!request.newPassword().equals(request.confirmPassword())) {
            throw new ResetPasswordException("Password and confirmation password do not match");
        }

        // check if the token exists or not
        PasswordResetRequest passwordResetRequest = passwordResetRepository.findBy(
                    byToken(request.token()), FluentQuery.FetchableFluentQuery::first)
                .orElseThrow(() -> new ResetPasswordException("Token not found"));
        // check if the token still valid or not
        if(passwordResetRequest.getValidUntil().isBefore(LocalDateTime.now())) {
            throw new ResetPasswordException("Token expired");
        }
        // check successfully update the new password to the account
        Employee e = passwordResetRequest.getEmployee();
        employeeRepository.updatePasswordById(e.getId(), passwordEncoder.encode(request.newPassword()));
        // delete all reset password request corresponding to the account after reset successfully
        passwordResetRepository.delete(byEmployeeId(e.getId()));

        return new ChangePasswordResponse("Reset password successfully!");
    }
}
