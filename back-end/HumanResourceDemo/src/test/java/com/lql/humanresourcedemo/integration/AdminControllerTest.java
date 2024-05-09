package com.lql.humanresourcedemo.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewEmployeeRequest;
import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.LoginResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.service.mail.MailService;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MailService mailService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private ObjectMapper objectMapper;


    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.2-alpine");
    @Container
    @ServiceConnection
    static RedisContainer redis  = new RedisContainer(DockerImageName.parse("redis:7.2.4-alpine"));
    private String email = "admin@company.com";
    private String password = "admin";

    @Test
    public void createNewEmployeeTest() throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);

        String loginResponseString = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        LoginResponse loginResponse = objectMapper.readValue(loginResponseString, LoginResponse.class);


        CreateNewEmployeeRequest createNewEmployeeRequest = new CreateNewEmployeeRequest("long", "le qui", LocalDate.now().minusYears(2), "0123456789", "abc@gmail.com", 100D, Role.EMPLOYEE, 1L, null);

        String createNewEmployeeResponeString = mockMvc.perform(post("/admin/employees")
                        .header("Authorization", loginResponse.type() + " " + loginResponse.token())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createNewEmployeeRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();


        GetProfileResponse createNewEmployeeResponse = objectMapper.readValue(createNewEmployeeResponeString, GetProfileResponse.class);


        assertAll(
                () -> Assertions.assertEquals("long", createNewEmployeeResponse.firstName()),
                () -> Assertions.assertEquals("le qui", createNewEmployeeResponse.lastName()),
                () -> Assertions.assertEquals("longlq@company.com", createNewEmployeeResponse.email()),
                () -> assertThat(employeeRepository.findByEmail("longlq@company.com")).isPresent()
        );

        verify(mailService, times(1)).sendEmail(any(), anyString(), anyString());


    }
}
