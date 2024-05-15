package com.lql.humanresourcedemo.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lql.humanresourcedemo.dto.request.login.LoginRequest;
import com.lql.humanresourcedemo.dto.request.login.LogoutRequest;
import com.lql.humanresourcedemo.dto.response.login.LoginResponse;
import com.redis.testcontainers.RedisContainer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
class LoginControllerTest {
    @Autowired
    private MockMvc mockMvc;


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
     void loginTest_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.type").value("Bearer"),
                        jsonPath("$.role").value("ADMIN"),
                        jsonPath("$.token").exists()
                );
    }
    @Test
    void loginOutTest_Success() throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);

        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.type").value("Bearer"),
                        jsonPath("$.role").value("ADMIN"),
                        jsonPath("$.token").exists()
                ).andReturn();

        LoginResponse loginResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class);
        mockMvc.perform(get("/profile")
                        .header("Authorization", loginResponse.type() + " " + loginResponse.token()))
                .andExpectAll(
                        status().isOk()
                );

        LogoutRequest logoutRequest = new LogoutRequest(loginResponse.token());
        mockMvc.perform(post("/sign-out")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.success").value(true)
                );

        mockMvc.perform(get("/profile")
                .header("Authorization", loginResponse.type() + " " + loginResponse.token()))
                .andExpectAll(
                        status().isUnauthorized()
                );
    }

    @Test
    void loginTest_RequestNotValid() throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, null);
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists()
                );

    }
}
