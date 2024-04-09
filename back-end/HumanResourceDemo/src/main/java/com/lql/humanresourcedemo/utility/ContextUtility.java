package com.lql.humanresourcedemo.utility;

import com.lql.humanresourcedemo.security.MyAuthentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextUtility {

    public static Long getCurrentEmployeeId() {
        return ((MyAuthentication) SecurityContextHolder.getContext().getAuthentication()).getEmployeeId();
    }

    public static String getCurrentRequestIPAddress() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
