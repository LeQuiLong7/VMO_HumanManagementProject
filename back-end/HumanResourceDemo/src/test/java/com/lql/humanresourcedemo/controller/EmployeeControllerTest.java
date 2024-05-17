package com.lql.humanresourcedemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lql.humanresourcedemo.dto.request.employee.CreateSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.response.salary.SalaryRaiseResponse;
import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;

import static com.lql.humanresourcedemo.controller.ContextMock.mockSecurityContext;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnsecuredWebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {
    private final String BASE_URL = EmployeeController.class.getAnnotation(RequestMapping.class).value()[0];

    @MockBean
    private EmployeeService employeeService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ERROR_MESSAGE = "error message";

    @Test
    void createSalaryRaiseRequest_BodyNotValid_ExpectedSalaryMustNotBeNull() {
        mockSecurityContext(() -> {
            String requestBody = """ 
                    {
                        "description": ""
                    }
                    """;
            try {
                mockMvc.perform(post(BASE_URL + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error").value("Expected salary must not be null"),
                        jsonPath("$.time_stamp").exists()
                ).andDo(print());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void createSalaryRaiseRequest_BodyNotValid_ExpectedSalaryMustBeNumber() {
        mockSecurityContext(() -> {
            String requestBody = """ 
                    {
                        "expectedSalary": "abc",
                        "description": ""
                    }
                    """;
            try {
                mockMvc.perform(post(BASE_URL + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error").value("abc is not a valid value for expected salary"),
                        jsonPath("$.time_stamp").exists()
                ).andDo(print());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void createSalaryRaiseRequest_BodyNotValid_JsonMalformat() {
        mockSecurityContext(() -> {
            String requestBody = """ 
                    {
                        "expectedS
                        "description": ""
                    }
                    """;
            try {
                mockMvc.perform(post(BASE_URL + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error").value("Request body is not a valid json"),
                        jsonPath("$.time_stamp").exists()
                ).andDo(print());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    void createSalaryRaiseRequest_Success() {
        mockSecurityContext(() -> {

            String requestBody = """ 
                    {
                        "expectedSalary": "200",
                        "description": ""
                    }
                    """;
            when(employeeService.createSalaryRaiseRequest(anyLong(), any(CreateSalaryRaiseRequest.class)))
                    .thenReturn(Mockito.mock(SalaryRaiseResponse.class));
            try {
                mockMvc.perform(post(BASE_URL + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                ).andExpectAll(
                        status().isCreated()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
