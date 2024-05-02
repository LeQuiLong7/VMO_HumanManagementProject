package com.lql.humanresourcedemo.utility;

import com.lql.humanresourcedemo.enumeration.LeaveViolationCode;
import com.lql.humanresourcedemo.model.attendance.LeaveRequest;
import com.lql.humanresourcedemo.model.salary.SalaryRaiseRequest;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import com.lql.humanresourcedemo.service.validate.ValidateServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static com.lql.humanresourcedemo.constant.CompanyConstant.COMPANY_DOMAIN;


//@RequiredArgsConstructor
public class HelperUtility {
    private static final ValidateService validateService  = new ValidateServiceImpl();
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


    public static String buildLeaveWarningMessage(String recipientName, LocalDate date, LeaveViolationCode violationCode) {

        return """
                Dear %s,
                You have a time keeping record dated: %s that needs an explanation.
                Violation code: %s
                """.formatted(recipientName, date, violationCode);
    }

    public static String buildEmployeeWeeklyReport(String recipientName, LocalDate startDate, LocalDate endDate, Map<LeaveViolationCode, Integer> detail, String reportUrl) {

        return """
                Dear %s,
                This is your weekly report from %s to %s
                 %s
                Click here to view more detail: %s
                """.formatted(recipientName, startDate, endDate, detail.toString().replaceAll("[{}]", ""), reportUrl);

    }

    public static Pageable validateAndBuildPageRequest(String page, String pageSize, List<String> properties, List<String> order, Class<?> clazz) {

        validateService.validatePageRequest(page, pageSize, properties, order, clazz);


        return buildPageRequest(Integer.parseInt(page), Integer.parseInt(pageSize), properties, order, clazz);

    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();

        // Add fields declared in this class
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            fields.add(field);
        }

        // Recursively add fields from superclasses
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null) {
            List<Field> superClassFields = getAllFields(superClass);
            fields.addAll(superClassFields);
        }

        return fields;
    }

    public static   Pageable buildPageRequest(int page, int pageSize, List<String> properties, List<String> order, Class<?> clazz) {
        List<Sort.Order> sorts = new ArrayList<>();

        List<String> originalFieldName = getAllFields(clazz).stream().map(Field::getName).toList();
        List<String> lowerCaseFiledName = originalFieldName.stream().map(String::toLowerCase).toList();






        for (int i = 0; i < properties.size(); i++) {
            String property = originalFieldName.get(lowerCaseFiledName.indexOf(properties.get(i).toLowerCase()));
            try {
                Sort.Direction direction = Sort.Direction.valueOf(order.get(i).toUpperCase());
                sorts.add(new Sort.Order(direction, property, true, Sort.NullHandling.NULLS_LAST));
            } catch (IndexOutOfBoundsException | NullPointerException e) {

                sorts.add(new Sort.Order(Sort.Direction.ASC, property, true, Sort.NullHandling.NULLS_LAST));
            }

        }

        return PageRequest.of(page, pageSize, Sort.by(sorts));

    }
}
