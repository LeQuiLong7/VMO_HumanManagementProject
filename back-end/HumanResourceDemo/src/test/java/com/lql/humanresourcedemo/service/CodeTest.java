package com.lql.humanresourcedemo.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class CodeTest {

    @Test
    public void test() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        System.out.println(passwordEncoder.matches("admin", "$2a$10$e2cgzLiUBBkuRiQwsgV4JuNUEmaEmu5/l1KW1NqGvM6Xa5UTWdqrO"));
//        System.out.println(passwordEncoder.encode("pm"));
//        System.out.println(passwordEncoder.encode("employee"));
    }

    @Test
    public void test2() {
        System.out.println(String.format("%s  %s", "abc", null));
    }

    @Test
    public void test3() {

        LocalDateTime now = LocalDateTime.now();
        System.out.println(now.format(DateTimeFormatter.ISO_DATE));
        System.out.println(now.format(DateTimeFormatter.BASIC_ISO_DATE));
        System.out.println(now.format(DateTimeFormatter.ISO_DATE_TIME));
        System.out.println(now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        System.out.println(now.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss a")));
    }

    @Test
    public void test4() {

        String time = "12:21:09";

        System.out.println(LocalTime.parse(time));
//        System.out.println(LocalTime.now());
    }

}
