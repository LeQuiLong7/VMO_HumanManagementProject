package com.lql.humanresourcedemo.utility;

import com.lql.humanresourcedemo.constant.CompanyConstant;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.lql.humanresourcedemo.constant.CompanyConstant.COMPANY_DOMAIN;

public class HelperUtility {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a");


    public static String buildEmail(String firstName, String lastName) {

        lastName = Arrays.stream(lastName.split(" "))
                .map(s -> s.substring(0, 1).toLowerCase())
                .collect(Collectors.joining());

        return String.format("%s%s@%s", firstName.toLowerCase(), lastName, COMPANY_DOMAIN);
    }

    public static String buildEmailWithWildcard(String email) {

       return email.substring(0, email.indexOf("@")) + "%" + email.substring(email.indexOf("@"));
    }


    public static String emailWithIdentityNumber(String email, int count) {

        return email.substring(0, email.indexOf("@")) + count + email.substring(email.indexOf("@"));
    }

    public static String buildWelcomeMailMessage(String recipientName, String email, String password) {

        return """
                Dear %s,
                Welcome to our company!
                Your email address: %s
                Password: %s
                """.formatted(recipientName, email, password);
    }
    public static String buildSalaryProcessedMail(String recipientName, SalaryRaiseRequest raiseRequest) {
        return """
                    Dear %s,
                    Your salary raise request:  id: %s,
                                          created at: %s
                                          previous salary: %s, 
                                          expected salary: %s,
                                          description: %s
                     has been %s,
                     Your new salary now is: %s
                """.formatted(recipientName, raiseRequest.getId(), raiseRequest.getCreatedAt().format(formatter), raiseRequest.getCurrentSalary(), raiseRequest.getExpectedSalary(), raiseRequest.getDescription(), raiseRequest.getStatus(), raiseRequest.getNewSalary());
    }


    public static String buildLeaveRequestProcessedMail(String recipientName, LeaveRequest leaveRequest) {
        return """
                    Dear %s,
                    
                    Your leave request:   id: %s,
                                          leave date: %s
                                          leave type: %s                           
                                          reason: %s,
                     has been %s,
                """.formatted(recipientName, leaveRequest.getId(), leaveRequest.getDate(), leaveRequest.getType(), leaveRequest.getReason(), leaveRequest.getStatus());
    }

    public static String buildResetMailMessage(String recipientName, String email, String token, LocalDateTime validUntil) {

        return """
                Dear %s,
                A password request was requested for your email: %s
                Use this token to reset your password: %s
                The token will remain valid until: %s
                """.formatted(recipientName, email, token, validUntil.format(formatter));
    }
}
