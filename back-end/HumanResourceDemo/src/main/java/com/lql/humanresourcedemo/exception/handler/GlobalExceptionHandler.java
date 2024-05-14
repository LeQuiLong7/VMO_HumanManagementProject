package com.lql.humanresourcedemo.exception.handler;


import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.lql.humanresourcedemo.exception.model.aws.AWSException;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.file.FileException;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.exception.model.newaccount.NewAccountException;
import com.lql.humanresourcedemo.exception.model.paging.PagingException;
import com.lql.humanresourcedemo.exception.model.password.ChangePasswordException;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.exception.model.resetpassword.ResetPasswordException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.exception.model.tech.TechException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.lql.humanresourcedemo.utility.ContextUtility.getCurrentEmployeeId;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> loginExceptionHandler(LoginException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Login", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> methodArgumentNotValidHandler(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .toList();
        log.warn(buildLogMessage("Method argument", "Someone provide not valid arguments to an endpoint " + ex.getMessage(), request));
        return createResponseDetail(errors.toString().substring(1, errors.toString().length() - 1), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(EmployeeException.class)
    public ResponseEntity<Object> employeeNotFoundExceptionHandler(EmployeeException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Fetch", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(FileException.class)
    public ResponseEntity<Object> fileNotSupportExceptionHandler(FileException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("File upload", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage() , HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(ChangePasswordException.class)
    public ResponseEntity<Object> passwordExceptionHandler(ChangePasswordException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Password change", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }



    @ExceptionHandler(NewAccountException.class)
    public ResponseEntity<Object> newAccountExceptionHandler(NewAccountException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("New account", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage().substring(0, ex.getMessage().indexOf("-")), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ResetPasswordException.class)
    public ResponseEntity<Object> resetPasswordExceptionHandler(ResetPasswordException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Reset password", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LeaveRequestException.class)
    public ResponseEntity<Object> leaveRequestExceptionHandler(LeaveRequestException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Leave request", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SalaryRaiseException.class)
    public ResponseEntity<Object> salaryRaiseExceptionHandler(SalaryRaiseException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Salary raise", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ProjectException.class)
    public ResponseEntity<Object> projectExceptionHandler(ProjectException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Project", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PagingException.class)
    public ResponseEntity<Object> pagingExceptionHandler(PagingException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Paging ", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<Object> pagingExceptionHandler(PropertyReferenceException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Paging ", ex.getMessage(), request));
        return createResponseDetail(ex.getPropertyName() + " is not a valid sort property", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TechException.class)
    public ResponseEntity<Object> techExceptionHandler(TechException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Tech ", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AWSException.class)
    public ResponseEntity<Object> awsExceptionHandler(AWSException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("AWS ", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> httpMessageNotReadableExceptionHandler(HttpMessageNotReadableException ex, HttpServletRequest request) {

        ex.printStackTrace();
        Object value = ((InvalidFormatException) ex.getCause()).getValue();
        String fieldName = ((InvalidFormatException) ex.getCause()).getPath().get(0).getFieldName();

        log.warn(buildLogMessage(" ", ex.getMessage(), request));
        return createResponseDetail(String.format("%s is not a valid value for %s", value, convertCamelCaseToNormalWords(fieldName)), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MailException.class)
    public ResponseEntity<Object> mailExceptionHandler(MailException ex, HttpServletRequest request) {
        log.warn(buildLogMessage("Mail ", ex.getMessage(), request));
        return createResponseDetail(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    public static String convertCamelCaseToNormalWords(String camelCase) {
        StringBuilder normalWords = new StringBuilder();

        for (int i = 0; i < camelCase.length(); i++) {
            char currentChar = camelCase.charAt(i);

            if (Character.isUpperCase(currentChar) && i > 0) {
                normalWords.append(" " + Character.toLowerCase(currentChar));
            }else {
                normalWords.append(currentChar);
            }
        }

        return normalWords.toString();
    }



    private ResponseEntity<Object> createResponseDetail(String message, HttpStatus status) {
        Map<String, String> detail = new HashMap<>();
        detail.put("error", message);
        detail.put("time_stamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(detail, status);
    }
    private String buildLogMessage(String failedActionName, String exceptionMessage, HttpServletRequest request) {
        return String.format("%s error: %s - Uri: %s - Method: %s - IP address: %s - Current user: %s", failedActionName, exceptionMessage, request.getRequestURI(), request.getMethod(), request.getRemoteAddr(), getCurrentEmployeeId() == -1 ? "Anonymous" : getCurrentEmployeeId());
    }

}
