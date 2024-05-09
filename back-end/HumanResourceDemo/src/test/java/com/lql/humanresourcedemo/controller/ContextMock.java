package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.security.MyAuthentication;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
public class ContextMock {
    public static void mockSecurityContext(Runnable test) {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            test.run();
        }
    }
}
