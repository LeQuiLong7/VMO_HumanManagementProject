package com.lql.humanresourcedemo.constant;

import com.lql.humanresourcedemo.controller.LoginController;
import org.springframework.web.bind.annotation.RequestMapping;

public class SecurityConstants {
    public static final String LOGIN_URI = LoginController.class.getAnnotation(RequestMapping.class).value()[0];
}
