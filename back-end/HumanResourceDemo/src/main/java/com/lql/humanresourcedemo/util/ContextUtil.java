package com.lql.humanresourcedemo.util;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class ContextUtil {

    public static Long getCurrentEmployeeId() {

        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        if(authentication instanceof AnonymousAuthenticationToken) {
            return -1L;
        }
        return Long.parseLong(authentication.getName());
    }

}
