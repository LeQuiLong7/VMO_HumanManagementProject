package com.lql.humanresourcedemo.exception.handler;


import com.lql.humanresourcedemo.exception.model.FileNotSupportException;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.utility.FileUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<Object> loginExceptionHandler(LoginException ex) {
        log.warn("Login error: {}", ex.getMessage());
        Map<String, String> detail = new HashMap<>();
        detail.put("error", "Wrong email or password");
        detail.put("time_stamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(detail, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(FileNotSupportException.class)
    public ResponseEntity<Object> fileNotSupportExceptionHandler(FileNotSupportException ex) {
        log.warn("File upload error: {}", ex.getMessage());
        Map<String, String> detail = new HashMap<>();


        detail.put("error", ex.getMessage() + " - support types: " + FileUtility.supportImageExtension.toString());
        detail.put("time_stamp", LocalDateTime.now().toString());

        return new ResponseEntity<>(detail, HttpStatus.BAD_REQUEST);
    }
}
