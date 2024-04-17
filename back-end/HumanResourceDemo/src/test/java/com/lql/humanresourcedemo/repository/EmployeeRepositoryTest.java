package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.security.MyAuthentication;
import org.junit.jupiter.api.AfterEach;
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
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            Employee e1 = Employee.builder()
                    .id(1L)
                    .leaveDays((byte) 1)
                    .email("longlq@company.com")
                    .role(Role.EMPLOYEE)
                    .build();
            Employee e2 = Employee.builder()
                    .id(2L)
                    .leaveDays((byte) 1)
                    .email("longlq1@company.com")
                    .role(Role.EMPLOYEE)
                    .build();

            Employee e3 = Employee.builder()
                    .id(2L)
                    .leaveDays((byte) 1)
                    .email("admin@company.com")
                    .role(Role.ADMIN)
                    .build();

            employeeRepository.save(e1);
            employeeRepository.save(e2);
            employeeRepository.save(e3);
        }
    }

    @AfterEach
    public void tearDown() {
        employeeRepository.deleteAll();
    }


    @Test
    public void countByEmailLikeTest() {
        assertEquals(2, employeeRepository.countByEmailLike("longlq%@company.com"));
    }

    @Test
    void getAllIdByQuitIsFalse() {

        assertEquals(3, employeeRepository.findByQuitIsFalse(Employee.class).size());
    }

    @Test
    void increaseLeaveDaysBy1() {
        employeeRepository.increaseLeaveDaysBy1();

        employeeRepository.findAll().forEach(employee -> {
            assertEquals(2, (int) employee.getLeaveDays());
        });
    }

    @Test
    void decreaseLeaveDaysBy1() {
        employeeRepository.decreaseLeaveDaysBy1(1L);

        assertEquals(0, (int) employeeRepository.findById(1L).get().getLeaveDays());
    }

}