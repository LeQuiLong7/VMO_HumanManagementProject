package com.lql.humanresourcedemo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lql.humanresourcedemo.dto.request.employee.CreateResetPasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.ResetPasswordRequest;
import com.lql.humanresourcedemo.dto.response.ChangePasswordResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.resetpassword.ResetPasswordException;
import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.security.MyAuthentication;
import com.lql.humanresourcedemo.service.password.PasswordService;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.lql.humanresourcedemo.controller.ContextMock.mockSecurityContext;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ResetPasswordController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class ResetPasswordControllerTest {

    @MockBean
    private PasswordService passwordService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ERROR_MESSAGE = "error message";
    private final String SUCCESS_MESSAGE = "success message";

    @Test
    void createResetPasswordRequest_ValidationFail() {
        mockSecurityContext(() -> {
            String email = "";
            CreateResetPasswordRequest request = new CreateResetPasswordRequest(email);

            String url = ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0];
            try {
                mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error").value("email: must not be blank"),
                        jsonPath("$.time_stamp").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    void createResetPasswordRequest_ServiceFail() {
        mockSecurityContext(() -> {
            String email = "longlq@company.com";
            CreateResetPasswordRequest request = new CreateResetPasswordRequest(email);

            when(passwordService.createPasswordResetRequest(email))
                    .thenThrow(new EmployeeException(ERROR_MESSAGE));

            String url = ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0];
            try {
                mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error").value(ERROR_MESSAGE),
                        jsonPath("$.time_stamp").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    void createResetPasswordRequest_Success() throws Exception {

        String email = "longlq@company.com";
        CreateResetPasswordRequest request = new CreateResetPasswordRequest(email);

        when(passwordService.createPasswordResetRequest(email))
                .thenReturn(new ChangePasswordResponse(SUCCESS_MESSAGE));

        String url = ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0];

        mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isCreated(),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(SUCCESS_MESSAGE)
        );

    }


    @Test
    void performResetPassword_ServiceFail() {
        mockSecurityContext(() -> {
            ResetPasswordRequest request = new ResetPasswordRequest("a", "a", "a");

            when(passwordService.resetPassword(request))
                    .thenThrow(new ResetPasswordException(ERROR_MESSAGE));

            String url = ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0];
            try {
                mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error").value(ERROR_MESSAGE),
                        jsonPath("$.time_stamp").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void performResetPassword_Success() throws Exception {


        ResetPasswordRequest request = new ResetPasswordRequest("a", "a", "a");

        when(passwordService.resetPassword(request))
                .thenReturn(new ChangePasswordResponse(SUCCESS_MESSAGE));

        String url = ResetPasswordController.class.getAnnotation(RequestMapping.class).value()[0];
        mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(SUCCESS_MESSAGE)
        );
    }

}