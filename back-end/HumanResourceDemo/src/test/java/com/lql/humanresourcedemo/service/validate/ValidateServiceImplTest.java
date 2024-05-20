package com.lql.humanresourcedemo.service.validate;

import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.exception.model.tech.TechException;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.project.ProjectRepository;
import com.lql.humanresourcedemo.repository.tech.TechRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ValidateServiceImplTest {
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TechRepository techRepository;

    private ValidateService validateService;

    @BeforeEach
    void setUp() {
        validateService = new ValidateServiceImpl(employeeRepository, projectRepository, techRepository);
    }

    @Test
    void requireExistsEmployee_EmployeeExists() {
        Long employeeId = 1L;
        when(employeeRepository.existsById(employeeId)).thenReturn(true);

        // No exception should be thrown
        assertDoesNotThrow(() -> validateService.requireExistsEmployee(employeeId));

    }

    @Test
    void requireExistsEmployee_EmployeeDoesNotExist() {
        Long employeeId = 1L;
        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        assertThrows(
                EmployeeException.class,
                () -> validateService.requireExistsEmployee(employeeId),
                "Could not find employee " + employeeId
        );
    }

    @Test
    void requireExistsProject_ProjectExists() {
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(true);

        // No exception should be thrown
        assertDoesNotThrow(() -> validateService.requireExistsProject(projectId));
    }

    @Test
    void requireExistsProject_ProjectDoesNotExist() {
        Long projectId = 1L;
        when(projectRepository.existsById(projectId)).thenReturn(false);

        assertThrows(
                ProjectException.class,
                () -> validateService.requireExistsProject(projectId),
                "Could not find project " + projectId
        );
    }

    @Test
    void requireExistsTech_TechExists() {
        Long techId = 1L;
        when(techRepository.existsById(techId)).thenReturn(true);

        // No exception should be thrown
        assertDoesNotThrow(() -> validateService.requireExistsTech(techId));
    }

    @Test
    void requireExistsTech_TechDoesNotExist() {
        Long techId = 1L;
        when(techRepository.existsById(techId)).thenReturn(false);

        assertThrows(
                TechException.class,
                () -> validateService.requireExistsTech(techId),
                "Tech id %s not found".formatted(techId)
        );
    }
}