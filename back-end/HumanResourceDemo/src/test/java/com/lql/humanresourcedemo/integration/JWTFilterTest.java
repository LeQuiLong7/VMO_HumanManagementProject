package com.lql.humanresourcedemo.integration;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.lql.humanresourcedemo.dto.model.employee.OnlyIdPasswordAndRole;
import com.lql.humanresourcedemo.dto.response.login.LoginResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.service.jwt.JWTServiceImpl;
import com.lql.humanresourcedemo.service.jwt.JwtService;
import com.redis.testcontainers.RedisContainer;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import org.junit.jupiter.api.BeforeEach;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureMockMvc
public class JWTFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtParser jwtParser;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtBuilder jwtBuilder;

    @Autowired
    private JwtService jwtService;


    @BeforeEach
    void setUp() {
        jwtService = new JWTServiceImpl(jwtBuilder,jwtParser, 60000, "MINUTES");
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer<>("postgres:16.2-alpine");
    @Container
    @ServiceConnection
    static RedisContainer redis  = new RedisContainer(DockerImageName.parse("redis:7.2.4-alpine"));

    @Test
    public void noBearerTokenTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/profile"))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertEquals("No bearer token", mvcResult.getResponse().getContentAsString());

    }

    @Test
    public void tokenNotValidTest() throws Exception {

        MvcResult mvcResult = mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer abc"))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();

        assertEquals("token is not valid", mvcResult.getResponse().getContentAsString());

    }
    @Test
    public void tokenExpired() throws Exception {
        jwtService = new JWTServiceImpl(jwtBuilder,jwtParser, -60000, "MINUTES");

        String expiredToken = jwtService.generateToken(new OnlyIdPasswordAndRole(1L, "", Role.EMPLOYEE));

        MvcResult mvcResult = mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + expiredToken))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();

        assertEquals("token expired", mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void unauthorizedAccess() throws Exception {
        String loginRequest = """
                { "email": "employee@company.com",
                   "password": "employee"
                }
                """;
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andReturn();
        LoginResponse loginResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class);

        MvcResult mvcResult = mockMvc.perform(get("/admin/employees")
                        .header("Authorization", "Bearer " + loginResponse.token()))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andReturn();

        assertEquals("Access denied. You don't have permission to access this resource.", mvcResult.getResponse().getContentAsString());
    }
    @Test
    public void loggedOutToken() throws Exception {
        String loginRequest = """
                { "email": "employee@company.com",
                   "password": "employee"
                }
                """;
        String logoutRequest = """
                {
                   "token": "%s"
                }
                """;
        MvcResult loginResult = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginRequest))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(loginResult.getResponse().getContentAsString(), LoginResponse.class);

        mockMvc.perform(post("/sign-out")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(logoutRequest.formatted(loginResponse.token())))
                .andReturn();


        MvcResult mvcResult = mockMvc.perform(get("/profile")
                        .header("Authorization", "Bearer " + loginResponse.token()))
                .andExpect(status().isUnauthorized())
                .andDo(print())
                .andReturn();

        assertEquals("Logged out token", mvcResult.getResponse().getContentAsString());
    }
}
