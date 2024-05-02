package com.lql.humanresourcedemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.security.MyAuthentication;
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
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = LeaveController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class LeaveControllerTest {
    @MockBean
    private LeaveService leaveService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ERROR_MESSAGE = "error message";
    private final String SUCCESS_MESSAGE = "success message";

    @Test
    void createLeaveRequest_Fail() throws Exception {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            objectMapper.registerModule(new JavaTimeModule());

            LeaveRequestt request = new LeaveRequestt(LocalDate.now(), "", LeaveType.UNPAID);

            when(leaveService.createLeaveRequest(anyLong(), any(LeaveRequestt.class)))
                    .thenThrow(new LeaveRequestException(ERROR_MESSAGE));

            String url = LeaveController.class.getAnnotation(RequestMapping.class).value()[0];

            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpectAll(
                    status().isBadRequest(),
                    jsonPath("$.error").exists(),
                    jsonPath("$.error").value("leaveDate: must be a future date"),
                    jsonPath("$.time_stamp").exists()
            );
        }
    }

    @Test
    void createLeaveRequest_Success() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            objectMapper.registerModule(new JavaTimeModule());

            LeaveRequestt request = new LeaveRequestt(LocalDate.now().plusDays(2), "", LeaveType.UNPAID);

            when(leaveService.createLeaveRequest(anyLong(), any(LeaveRequestt.class)))
                    .thenReturn(new LeaveResponse(1L, 1L, "", "", LocalDate.now(), LocalDateTime.now(), LeaveType.UNPAID, "", LeaveStatus.PROCESSING, null));

            String url = LeaveController.class.getAnnotation(RequestMapping.class).value()[0];

            mockMvc.perform(post(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            ).andExpectAll(
                    status().isOk(),
                    jsonPath("$.date").exists(),
                    jsonPath("$.reason").exists(),
                    jsonPath("$.employeeId").exists(),
                    jsonPath("$.type").exists(),
                    jsonPath("$.status").exists()
            );
        }
    }
    @Test
    void getAllLeaveRequestTest() throws Exception {

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);

            when(leaveService.getAllLeaveRequest(any(), any(Pageable.class)))
                    .thenReturn(Page.empty());

            String url = LeaveController.class.getAnnotation(RequestMapping.class).value()[0];

            mockMvc.perform(get(url)
            ).andExpectAll(
                    status().isOk(),
                    jsonPath("$.content").exists()
            );
        }
    }


}