package com.lql.humanresourcedemo.utility;

import com.lql.humanresourcedemo.constant.CompanyConstant;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.lql.humanresourcedemo.constant.CompanyConstant.COMPANY_DOMAIN;

public class HelperUtility {

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
}
