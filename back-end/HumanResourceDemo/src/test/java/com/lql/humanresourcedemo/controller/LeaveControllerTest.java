package com.lql.humanresourcedemo.controller;

import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.service.leave.LeaveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.Optional;

import static com.lql.humanresourcedemo.controller.ContextMock.mockSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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