package com.lql.humanresourcedemo.constant;

import com.lql.humanresourcedemo.controller.ResetPasswordController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;

public class SecurityConstants {
    public static final List<String> PUBLIC_URI = new ArrayList<>();

    static {
        PUBLIC_URI.addAll(List.of("/login", "/sign-out", "/exchange"));
        PUBLIC_URI.add("/oauth2/authorization/google");
        PUBLIC_URI.add(ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0]);
        PUBLIC_URI.add("/swagger-ui/**");
        PUBLIC_URI.add("/swagger-ui.html");
        PUBLIC_URI.add("/v3/api-docs/**");
    }
}
