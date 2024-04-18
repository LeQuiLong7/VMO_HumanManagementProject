package com.lql.humanresourcedemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lql.humanresourcedemo.dto.request.employee.LoginRequest;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.login.LoginException;
import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.security.MyAuthentication;
import com.lql.humanresourcedemo.security.SecurityConfig;
import com.lql.humanresourcedemo.service.login.LoginService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
//import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = LoginController.class, excludeAutoConfiguration = SecurityConfig.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {
    @MockBean
    private LoginService loginService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();





    @Test
    public void LoginFailTest() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);


            LoginRequest loginRequest = new LoginRequest("a", "a");

            when(loginService.login(any(LoginRequest.class)))
                    .thenThrow(new LoginException("%s doesn't exists".formatted(loginRequest.email())));


            String url = LoginController.class.getAnnotation(RequestMapping.class).value()[0];
            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(loginRequest))
            ).andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.error").exists(),
                    jsonPath("$.time_stamp").exists()
            ).andDo(print());



        }



    }

}