package com.lql.humanresourcedemo.constant;

import com.lql.humanresourcedemo.controller.LoginController;
import com.lql.humanresourcedemo.controller.ResetPasswordController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

public class SecurityConstants {
//    public static final String LOGIN_URI = LoginController.class.getAnnotation(RequestMapping.class).value()[0];
//    public static final String RESET_PASSWORD_URI = ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0];
//


    public static final List<String> PUBLIC_URI = new ArrayList<>();

    static {
        PUBLIC_URI.add(LoginController.class.getAnnotation(RequestMapping.class).value()[0]);
        PUBLIC_URI.add(ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0]);
    }
}
