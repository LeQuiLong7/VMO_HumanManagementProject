package com.lql.humanresourcedemo.service.tech;

import com.lql.humanresourcedemo.dto.model.tech.TechStack;
import com.lql.humanresourcedemo.dto.request.admin.UpdateEmployeeTechStackRequest;
import com.lql.humanresourcedemo.dto.response.tech.TechStackResponse;
import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.tech.TechException;
import com.lql.humanresourcedemo.model.tech.EmployeeTech;
import com.lql.humanresourcedemo.model.tech.Tech;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.tech.EmployeeTechRepository;
import com.lql.humanresourcedemo.repository.tech.TechRepository;
import com.lql.humanresourcedemo.service.validate.ValidateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("unchecked")
class TechServiceImplTest {
    @Mock
    private EmployeeTechRepository employeeTechRepository;
    @Mock
    private TechRepository techRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private ValidateService validateService;
    private TechService techService;

    @BeforeEach
    void setUp() {

        techService = new TechServiceImpl(employeeTechRepository, techRepository, employeeRepository, validateService);
    }

    @Test
    void getAllTech() {
        Pageable pageable = Pageable.unpaged();
        Page<Tech> techPage = new PageImpl<>(Collections.emptyList());
        when(techRepository.findAll(pageable)).thenReturn(techPage);

        Page<Tech> result = techService.getAllTech(pageable);

        assertNotNull(result);
        assertEquals(techPage, result);
        verify(techRepository, times(1)).findAll(pageable);
    }

    @Test
    void getTechStackByEmployeeId_EmployeeExists() {
        Long empId = 1L;
        List<EmployeeTech> employeeTechList = Collections.emptyList();

        when(employeeTechRepository.findBy(any(Specification.class), any())).thenReturn(employeeTechList);

        TechStackResponse response = techService.getTechStackByEmployeeId(empId);

        assertNotNull(response);
        assertEquals(empId, response.employeeId());
        assertTrue(response.techInfo().isEmpty());
        verify(validateService, times(1)).requireExistsEmployee(empId);
        verify(employeeTechRepository, times(1)).findBy(any(Specification.class), any());
    }

    @Test
    void getTechStackByEmployeeId_EmployeeNotFound() {
        Long empId = 1L;
        doThrow(new EmployeeException(empId)).when(validateService).requireExistsEmployee(empId);

        EmployeeException exception = assertThrows(EmployeeException.class, () -> techService.getTechStackByEmployeeId(empId));

        assertEquals("Could not find employee " + empId, exception.getMessage());
        verify(validateService, times(1)).requireExistsEmployee(empId);
        verifyNoInteractions(employeeTechRepository);
    }

    @Test
    void updateEmployeeTechStack_EmployeeAndTechExist() {
        TechStack techStack = new TechStack(1L, 5D);
        UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(1L, List.of(techStack));

        // Mocking repository responses

        when(employeeTechRepository.findBy(any(Specification.class), any())).thenReturn(Collections.emptyList());

        TechStackResponse response = techService.updateEmployeeTechStack(request);

        assertNotNull(response);
        assertEquals(request.employeeId(), response.employeeId());
        assertEquals(1, response.techInfo().size());
        assertEquals(techStack.techId(), response.techInfo().get(0).techId());
        assertEquals(techStack.yearOfExperience(), response.techInfo().get(0).yearOfExperience());

        verify(validateService, times(1)).requireExistsEmployee(request.employeeId());
        verify(employeeTechRepository, times(1)).findBy(any(Specification.class), any());
        verify(validateService, times(request.techStacks().size())).requireExistsTech(any());
        verify(employeeTechRepository, times(request.techStacks().size())).save(any(EmployeeTech.class));
    }

    @Test
    void updateEmployeeTechStack_EmployeeNotFound() {
        UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(1L, List.of(new TechStack(1L, 5D)));

        doThrow(new EmployeeException(request.employeeId())).when(validateService).requireExistsEmployee(request.employeeId());

        assertThrows(
                EmployeeException.class,
                () -> techService.updateEmployeeTechStack(request),
                "Could not find employee " + request.employeeId()
        );

        verify(validateService, times(1)).requireExistsEmployee(request.employeeId());
        verifyNoInteractions(employeeTechRepository);
        verifyNoInteractions(techRepository);

    }

    @Test
    void updateEmployeeTechStack_TechNotFound() {
        TechStack techStack = new TechStack(1L, 5D);
        UpdateEmployeeTechStackRequest request = new UpdateEmployeeTechStackRequest(1L, List.of(techStack));

        doThrow(new TechException("Tech id %s not found".formatted(techStack.techId()))).when(validateService).requireExistsTech(techStack.techId());

        assertThrows(
                TechException.class,
                () -> techService.updateEmployeeTechStack(request),
                "Tech id %s not found".formatted(techStack.techId())
        );

        verify(validateService, times(1)).requireExistsEmployee(request.employeeId());
        verify(validateService, times(1)).requireExistsTech(techStack.techId());
        verifyNoInteractions(employeeTechRepository);
        verifyNoInteractions(techRepository);

    }


}