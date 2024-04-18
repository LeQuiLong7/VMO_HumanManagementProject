package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.enumeration.Role;
import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.security.MyAuthentication;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:16.2");


    Employee e1 = Employee.builder()
            .leaveDays((byte) 1)
            .email("longlq@company.com")
            .role(Role.EMPLOYEE)
            .quit(false)
            .build();
    Employee e2 = Employee.builder()
            .leaveDays((byte) 1)
            .email("longlq1@company.com")
            .quit(false)
            .role(Role.EMPLOYEE)
            .build();


    @BeforeEach
    public void setup() {
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(new MyAuthentication(1L, Role.ADMIN));

        try (MockedStatic<SecurityContextHolder> utilities = Mockito.mockStatic(SecurityContextHolder.class)) {
            utilities.when(SecurityContextHolder::getContext).thenReturn(securityContext);


            employeeRepository.saveAndFlush(e1);
            employeeRepository.saveAndFlush(e2);

            Employee e3 = Employee.builder()
                    .leaveDays((byte) 1)
                    .email("admin@company.com")
                    .role(Role.ADMIN)
                    .quit(false)
                    .managedBy(e1)
                    .build();


            employeeRepository.saveAndFlush(e3);
        }
    }

    @Test
    public void countByEmailLikeTest() {
        assertEquals(2, employeeRepository.countByEmailLike("longlq%@company.com"));
    }

    @Test
    void getAllIdByQuitIsFalse() {

        assertEquals(3, employeeRepository.findByQuitIsFalse(Employee.class).size());
    }

//    @Test
//    void increaseLeaveDaysBy1() throws InterruptedException {
//        employeeRepository.increaseLeaveDaysBy1();
//
//        employeeRepository.findAll().forEach(employee -> {
//            assertEquals(2, (int) employee.getLeaveDays());
//        });
//    }
//
//    @Test
//    void decreaseLeaveDaysBy1() {
//        employeeRepository.decreaseLeaveDaysBy1(e1.getId());
////        employeeRepository.flush();
//
//        assertEquals(0, (int) employeeRepository.findById(e1.getId()).get().getLeaveDays());
//    }

    @Test
    void getManagedIdById() {
        assertEquals(1, employeeRepository.findAllIdByManagedById(e1.getId()).size());
    }
}