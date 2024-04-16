package com.lql.humanresourcedemo.service;

import com.lql.humanresourcedemo.enumeration.LeaveViolationCode;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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


    @Test
    public void test5() {

        Map<LeaveViolationCode, Integer> detail = new HashMap<>();

        detail.put(LeaveViolationCode.ON_TIME, 5);
        detail.put(LeaveViolationCode.ABSENCE, 1);
        System.out.println(detail.toString().replaceAll("[{}]", ""));
//        System.out.println(LocalTime.now());
    }

    @Test
    public void test6() {

        System.out.println(Sort.Direction.valueOf("asc"));
//        System.out.println(LocalTime.now());
    }
}
