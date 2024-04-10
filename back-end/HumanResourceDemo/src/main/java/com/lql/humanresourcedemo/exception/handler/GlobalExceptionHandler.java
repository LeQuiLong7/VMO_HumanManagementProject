package com.lql.humanresourcedemo.exception.handler;


import com.lql.humanresourcedemo.exception.model.employee.EmployeeNotFoundException;
import com.lql.humanresourcedemo.exception.model.file.FileNotSupportException;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.exception.model.newaccount.NewAccountException;
import com.lql.humanresourcedemo.exception.model.password.PasswordException;
import com.lql.humanresourcedemo.utility.ContextUtility;
import com.lql.humanresourcedemo.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.lql.humanresourcedemo.utility.ContextUtility.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> loginExceptionHandler(LoginException ex) {
        log.warn(buildLogMessage("Login", ex.getMessage()));
        return createResponseDetail("Wrong email or password", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    public ResponseEntity<Object> employeeNotFoundExceptionHandler(EmployeeNotFoundException ex) {
        log.warn(buildLogMessage("Fetch", ex.getMessage()));
        return createResponseDetail("Could not find employee", HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(FileNotSupportException.class)
    public ResponseEntity<Object> fileNotSupportExceptionHandler(FileNotSupportException ex) {
        log.warn(buildLogMessage("File upload", ex.getMessage()));
        return createResponseDetail(ex.getMessage() + " - support types: " + FileUtility.supportImageExtension.toString(), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(PasswordException.class)
    public ResponseEntity<Object> passwordExceptionHandler(PasswordException ex) {
        log.warn(buildLogMessage("Password change", ex.getMessage()));
        return createResponseDetail(ex.getMessage().substring(0, ex.getMessage().indexOf("-")), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NewAccountException.class)
    public ResponseEntity<Object> newAccountExceptionHandler(NewAccountException ex) {
        log.warn(buildLogMessage("New account", ex.getMessage()));
        return createResponseDetail(ex.getMessage().substring(0, ex.getMessage().indexOf("-")), HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<Object> createResponseDetail(String message, HttpStatus status) {
        Map<String, String> detail = new HashMap<>();
        detail.put("error", message);
        detail.put("time_stamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(detail, status);
    }
    private String buildLogMessage(String failedActionName, String exceptionMessage) {
        return String.format("%s error: %s - IP address: %s", failedActionName, exceptionMessage, getCurrentRequestIPAddress());
    }

}
