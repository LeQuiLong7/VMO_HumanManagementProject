package com.lql.humanresourcedemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lql.humanresourcedemo.dto.request.employee.LeaveRequestt;
import com.lql.humanresourcedemo.dto.response.LeaveResponse;
import com.lql.humanresourcedemo.enumeration.LeaveStatus;
import com.lql.humanresourcedemo.enumeration.LeaveType;
import com.lql.humanresourcedemo.exception.model.leaverequest.LeaveRequestException;
import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.filter.Oauth2AuthenticationSuccessHandler;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.lql.humanresourcedemo.controller.ContextMock.mockSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@UnsecuredWebMvcTest(LeaveController.class)
@AutoConfigureMockMvc(addFilters = false)
class LeaveControllerTest {
    private final String BASE_URL = LeaveController.class.getAnnotation(RequestMapping.class).value()[0];
    
    @MockBean
    private LeaveService leaveService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ERROR_MESSAGE = "error message";

    @Test
    void createLeaveRequest_Fail()  {
            mockSecurityContext(() -> {
                objectMapper.registerModule(new JavaTimeModule());
                LeaveRequestt request = new LeaveRequestt(LocalDate.now(), "", LeaveType.UNPAID);

                when(leaveService.createLeaveRequest(anyLong(), any(LeaveRequestt.class)))
                        .thenThrow(new LeaveRequestException(ERROR_MESSAGE));


                try {
                    mockMvc.perform(post(BASE_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    ).andExpectAll(
                            status().isBadRequest(),
                            jsonPath("$.error").exists(),
                            jsonPath("$.error").value("leaveDate: must be a future date"),
                            jsonPath("$.time_stamp").exists()
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });


    }

    @Test
    void createLeaveRequest_Success() throws Exception {
        mockSecurityContext(() -> {
            objectMapper.registerModule(new JavaTimeModule());
            LeaveRequestt request = new LeaveRequestt(LocalDate.now().plusDays(2), "", LeaveType.UNPAID);

            when(leaveService.createLeaveRequest(anyLong(), any(LeaveRequestt.class)))
                    .thenReturn(new LeaveResponse(1L, 1L, "", "", LocalDate.now(), LocalDateTime.now(), LeaveType.UNPAID, "", LeaveStatus.PROCESSING, null));


            try {
                mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isCreated(),
                        jsonPath("$.date").exists(),
                        jsonPath("$.reason").exists(),
                        jsonPath("$.employeeId").exists(),
                        jsonPath("$.type").exists(),
                        jsonPath("$.status").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void getAllLeaveRequestTest(){

        mockSecurityContext(() -> {
            when(leaveService.getAllLeaveRequest(any(), any(Pageable.class)))
                    .thenReturn(Page.empty());
            try {
                mockMvc.perform(get(BASE_URL)
                ).andExpectAll(
                        status().isOk(),
                        jsonPath("$.content").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void getLeaveRequestByDateAndEmployeeId_NotFound()  {

        mockSecurityContext(() -> {
            when(leaveService.getLeaveRequestByDateAndEmployeeId(any(), any()))
                    .thenReturn(Optional.empty());

            LocalDate now = LocalDate.now();
            try {
                MvcResult result = mockMvc.perform(get(BASE_URL + "/%s".formatted(now.toString())))
                        .andExpect(status().isOk())
                        .andReturn();
                assertEquals("null", result.getResponse().getContentAsString());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


}