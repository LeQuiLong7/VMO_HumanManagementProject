package com.lql.humanresourcedemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewEmployeeRequest;
import com.lql.humanresourcedemo.dto.request.admin.CreateNewProjectRequest;
import com.lql.humanresourcedemo.dto.request.admin.HandleSalaryRaiseRequest;
import com.lql.humanresourcedemo.dto.request.admin.UpdateEmployeeTechStackRequest;
import com.lql.humanresourcedemo.dto.response.GetProfileResponse;
import com.lql.humanresourcedemo.dto.response.ProjectResponse;
import com.lql.humanresourcedemo.dto.response.TechStackResponse;
import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.enumeration.SalaryRaiseRequestStatus;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.salaryraise.SalaryRaiseException;
import com.lql.humanresourcedemo.filter.JWTAuthenticationFilter;
import com.lql.humanresourcedemo.security.MyAuthentication;
import com.lql.humanresourcedemo.service.admin.AdminService;
import com.lql.humanresourcedemo.service.employee.EmployeeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import java.util.Collections;
import java.util.List;

import static com.lql.humanresourcedemo.controller.ContextMock.mockSecurityContext;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = AdminController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @MockBean
    private AdminService adminService;

    @MockBean
    private JWTAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String ERROR_MESSAGE = "error message";
    private final String SUCCESS_MESSAGE = "success message";

    @ParameterizedTest(name = "{0} is not a valid page number")
    @ValueSource(strings = {"abc", "-1", "4.5"})
    void getAllEmployee_PageRequestNotValid(String page) {
        mockSecurityContext(() -> {
            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
            try {
                mockMvc.perform(get(url + "/employees?page=" + page)
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.error").value(page+ " is not a valid page number"),
                        jsonPath("$.time_stamp").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    @Test
    void createNewEmployee_RequestBodyNotValid() {
        mockSecurityContext(() -> {
            CreateNewEmployeeRequest request
                    = new CreateNewEmployeeRequest(null, null, null, null, null, null, null, null, null);
            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];

            try {
                mockMvc.perform(post(url + "/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.time_stamp").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void getAllProjectByEmployeeId_Fail() {
        mockSecurityContext(() -> {
            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
            when(adminService.getAllProjectsByEmployeeId(anyLong(), any(Pageable.class)))
                    .thenThrow(new EmployeeException(ERROR_MESSAGE));
            try {
                mockMvc.perform(get(url + "/employee/1/projects")
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
    void getAllPM_Success() {
            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
            when(adminService.getAllPM(any(Pageable.class)))
                    .thenReturn(Mockito.mock(Page.class));
            try {
                mockMvc.perform(get(url + "/pm")
                ).andExpectAll(
                        status().isOk()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @Test
    void getAllTechStack_Success() {
            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
            when(adminService.getAllTech(any(Pageable.class)))
                    .thenReturn(Mockito.mock(Page.class));
            try {
                mockMvc.perform(get(url + "/techStack")
                ).andExpectAll(
                        status().isOk()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @Test
    void getTechStackByEmployeeId_Success() {
        String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
        when(adminService.getTechStackByEmployeeId(any(Long.class)))
                .thenReturn(Mockito.mock(TechStackResponse.class));
        try {
            mockMvc.perform(get(url + "/techStack/1")
            ).andExpectAll(
                    status().isOk()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void getTechStackByEmployeeId_EmployeeNotFound() {
        mockSecurityContext(() -> {

            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
            when(adminService.getTechStackByEmployeeId(any(Long.class)))
                    .thenThrow(new EmployeeException(ERROR_MESSAGE));
            try {
                mockMvc.perform(get(url + "/techStack/1")
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
    void updateTechStackForEmployee_RequestNotValid() {
        mockSecurityContext(() -> {

            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];

            UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(
                    1L,
                    Collections.emptyList()
            ) ;
            try {
                mockMvc.perform(put(url + "/techStack")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.time_stamp").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void updateTechStackForEmployee_EmployeeNotFound() {
        mockSecurityContext(() -> {

            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
            when(adminService.updateEmployeeTechStack(any(UpdateEmployeeTechStackRequest.class)))
                    .thenThrow(new EmployeeException(ERROR_MESSAGE));

            UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(
                    1L,
                    List.of(new TechStack(1L, 2.6))
            ) ;
            try {
                mockMvc.perform(put(url + "/techStack")
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
    void getAllSalaryRaiseRequest_Success() {
            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
            when(adminService.getAllSalaryRaiseRequest(any(Pageable.class)))
                    .thenReturn(Mockito.mock(Page.class));
            try {
                mockMvc.perform(get(url + "/salary")
                ).andExpectAll(
                        status().isOk()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @Test
    void handleSalaryRaise_RequestNotValid() {
        mockSecurityContext(() -> {

            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];

            HandleSalaryRaiseRequest request = new HandleSalaryRaiseRequest(
                    1L,
                    null,
                    -2.5
            ) ;
            try {
                mockMvc.perform(put(url + "/salary")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.time_stamp").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Test
    void handleSalaryRaise_Fail() {
        mockSecurityContext(() -> {

            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];

            HandleSalaryRaiseRequest request = new HandleSalaryRaiseRequest(
                    1L,
                    SalaryRaiseRequestStatus.FULLY_ACCEPTED,
                    500D
            ) ;
            when(adminService.handleSalaryRaiseRequest(anyLong(), any(HandleSalaryRaiseRequest.class)))
                    .thenThrow(new SalaryRaiseException(ERROR_MESSAGE));
            try {
                mockMvc.perform(put(url + "/salary")
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
    void getAllProjects() {
            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];
            when(adminService.getAllProject(any(Pageable.class)))
                    .thenReturn(Mockito.mock(Page.class));
            try {
                mockMvc.perform(get(url + "/project")
                ).andExpectAll(
                        status().isOk()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @Test
    void getAllEmployeesInsideProjects() {
    }

    @Test
    void createNewProject_RequestNotValid() {
        mockSecurityContext(() -> {

            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];

            CreateNewProjectRequest request = new CreateNewProjectRequest(
                    "",
                    null,
                    null,
                    null
            ) ;
            try {
                mockMvc.perform(post(url + "/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isBadRequest(),
                        jsonPath("$.error").exists(),
                        jsonPath("$.time_stamp").exists()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
    @Test
    void createNewProject_Success() {
        mockSecurityContext(() -> {
            objectMapper.registerModule(new JavaTimeModule());

            String url = AdminController.class.getAnnotation(RequestMapping.class).value()[0];

            CreateNewProjectRequest request = new CreateNewProjectRequest(
                    "a",
                    "",
                    LocalDate.now().plusDays(2),
                    LocalDate.now().plusDays(20)
            ) ;
            when(adminService.createNewProject(any(CreateNewProjectRequest.class)))
                    .thenReturn(Mockito.mock(ProjectResponse.class));
            try {
                mockMvc.perform(post(url + "/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                ).andExpectAll(
                        status().isCreated()
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


    @Test
    void updateProjectState() {
    }

    @Test
    void assignEmployeeToProject() {
    }
}