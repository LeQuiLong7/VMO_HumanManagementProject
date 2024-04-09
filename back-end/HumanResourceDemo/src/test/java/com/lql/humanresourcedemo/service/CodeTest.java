package com.lql.humanresourcedemo.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class CodeTest {

    @Test
    public void test() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        System.out.println(passwordEncoder.matches("admin", "$2a$10$e2cgzLiUBBkuRiQwsgV4JuNUEmaEmu5/l1KW1NqGvM6Xa5UTWdqrO"));
//        System.out.println(passwordEncoder.encode("pm"));
//        System.out.println(passwordEncoder.encode("employee"));
    }
}
