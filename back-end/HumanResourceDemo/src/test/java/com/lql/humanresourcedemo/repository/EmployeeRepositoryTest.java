package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.security.MyAuthentication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16.2");

    @BeforeEach
    public void setup() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

        securityContext.setAuthentication(new MyAuthentication(1L, null, null));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Employee e1 = Employee.builder()
                    .email("longlq@company.com")
                    .build();
            Employee e2 = Employee.builder()
                    .email("longlq1@company.com")
                    .build();

            employeeRepository.save(e1);
            employeeRepository.save(e2);
        }


    }


    @Test
    public void countByEmailLikeTest() {
        assertEquals(2, employeeRepository.countByEmailLike("longlq%@company.com"));
    }

}