package com.lql.humanresourcedemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.security.MyAuthentication;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@WebMvcTest(value = ProfileController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {
    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ERROR_MESSAGE = "error message";
    private final String SUCCESS_MESSAGE = "success message";

    @Test
    void getProfileTest() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(employeeService.getProfile(any()))
                    .thenReturn(Mockito.mock(GetProfileResponse.class));

            String url = ProfileController.class.getAnnotation(RequestMapping.class).value()[0];

            mockMvc.perform(get(url)
            ).andExpectAll(
                    status().isOk()
            );
        }
    }
    @Test
    void getTechStackTest() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(employeeService.getTechStack(any()))
                    .thenReturn(Mockito.mock(TechStackResponse.class));

            String url = ProfileController.class.getAnnotation(RequestMapping.class).value()[0];
            String path = ProfileController.class.getMethod("getTechStack").getAnnotation(GetMapping.class).value()[0];

            mockMvc.perform(get(url + path)
            ).andExpectAll(
                    status().isOk()
            );
        }
    }
    @Test
    void updateProfileTest_Success() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            UpdateProfileRequest request = new UpdateProfileRequest("", "", null, "", "abc@gmai.com");

            when(employeeService.updateInfo(any(), any()))
                    .thenReturn(Mockito.mock(GetProfileResponse.class));

            String url = ProfileController.class.getAnnotation(RequestMapping.class).value()[0];

            mockMvc.perform(put(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpectAll(
                    status().isOk()
            );
        }
    }
    @Test
    void updateProfileTest_EmailNotValid() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            UpdateProfileRequest request = new UpdateProfileRequest("", "", null, "", "a");

            when(employeeService.updateInfo(any(), any()))
                    .thenReturn(Mockito.mock(GetProfileResponse.class));

            String url = ProfileController.class.getAnnotation(RequestMapping.class).value()[0];

            mockMvc.perform(put(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.time_stamp").exists(),
                    jsonPath("$.error").exists()
            );
        }
    }


}