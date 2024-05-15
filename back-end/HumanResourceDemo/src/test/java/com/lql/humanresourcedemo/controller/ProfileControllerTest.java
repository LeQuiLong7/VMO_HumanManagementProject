package com.lql.humanresourcedemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lql.humanresourcedemo.dto.request.employee.ChangePasswordRequest;
import com.lql.humanresourcedemo.dto.request.employee.UpdateProfileRequest;
import com.lql.humanresourcedemo.dto.response.employee.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.exception.model.file.FileException;
import com.lql.humanresourcedemo.exception.model.password.ChangePasswordException;
import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.lql.humanresourcedemo.controller.ContextMock.mockSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnsecuredWebMvcTest(ProfileController.class)

@AutoConfigureMockMvc(addFilters = false)
class ProfileControllerTest {
    private final String BASE_URL = ProfileController.class.getAnnotation(RequestMapping.class).value()[0];

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ERROR_MESSAGE = "error message";

    @Test
    void getProfileTest() {

        mockSecurityContext(() -> {
            when(employeeService.getProfile(any()))
                    .thenReturn(Mockito.mock(GetProfileResponse.class));

            try {
                mockMvc.perform(get(BASE_URL)
                ).andExpectAll(
                        status().isOk()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void getTechStackTest() {


        mockSecurityContext(() -> {
            when(employeeService.getTechStack(any()))
                    .thenReturn(Mockito.mock(TechStackResponse.class));

            try {
                mockMvc.perform(get(BASE_URL + "/tech")
                ).andExpectAll(
                        status().isOk()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void updateProfileTest_Success() {


        mockSecurityContext(() -> {
            UpdateProfileRequest request = new UpdateProfileRequest("", "", null, "", "abc@gmai.com");

            when(employeeService.updateInfo(any(), any()))
                    .thenReturn(Mockito.mock(GetProfileResponse.class));

            try {
                mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isOk()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void updateProfileTest_EmailNotValid(){

        mockSecurityContext(() -> {
            UpdateProfileRequest request = new UpdateProfileRequest("", "", null, "", "a");

            when(employeeService.updateInfo(any(), any()))
                    .thenReturn(Mockito.mock(GetProfileResponse.class));

            try {
                mockMvc.perform(put(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.time_stamp").exists(),
                        jsonPath("$.error").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void uploadAvatar_FileNotSupported()  {

        mockSecurityContext(() -> {
            MockMultipartFile file = new MockMultipartFile("file", "filename.pdf", MediaType.APPLICATION_PDF_VALUE, "file content".getBytes());

            when(employeeService.uploadAvatar(any(), any()))
                    .thenThrow(new FileException(ERROR_MESSAGE));
            MockMultipartHttpServletRequestBuilder builder =
                    MockMvcRequestBuilders.multipart(BASE_URL + "/avatar");

            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            try {
                mockMvc.perform(builder
                                .file(file))
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.time_stamp").exists())
                        .andExpect(jsonPath("$.error").value(ERROR_MESSAGE));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }


        });
    }

    @Test
    void changePassword_RequestBodyNotValid()  {

        mockSecurityContext(() -> {
            ChangePasswordRequest request = new ChangePasswordRequest("", null, null);
            try {
                mockMvc.perform(put(BASE_URL + "/password")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request))
                        )
                        .andExpect(status().isBadRequest())
                        .andExpect(jsonPath("$.error").exists())
                        .andExpect(jsonPath("$.time_stamp").exists())
                        .andDo(print());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void changePassword_NewPasswordAndConfirmPasswordNotMatch() {

            mockSecurityContext(() -> {
                ChangePasswordRequest request = new ChangePasswordRequest("", "a", "b");

                when(employeeService.changePassword(any(), any()))
                        .thenThrow(new ChangePasswordException(ERROR_MESSAGE));
                try {
                    mockMvc.perform(put(BASE_URL + "/password")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(objectMapper.writeValueAsString(request))
                            )
                            .andExpect(status().isBadRequest())
                            .andExpect(jsonPath("$.error").exists())
                            .andExpect(jsonPath("$.error").value(ERROR_MESSAGE))
                            .andExpect(jsonPath("$.time_stamp").exists());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
    }

}