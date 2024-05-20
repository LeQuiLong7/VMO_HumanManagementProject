package com.lql.humanresourcedemo.service.validate;

import com.lql.humanresourcedemo.exception.model.employee.EmployeeException;
import com.lql.humanresourcedemo.exception.model.project.ProjectException;
import com.lql.humanresourcedemo.exception.model.tech.TechException;
import com.lql.humanresourcedemo.repository.employee.EmployeeRepository;
import com.lql.humanresourcedemo.repository.project.ProjectRepository;
import com.lql.humanresourcedemo.repository.tech.TechRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ValidateServiceImpl implements ValidateService {

    private final EmployeeRepository employeeRepository;
    private final ProjectRepository projectRepository;
    private final TechRepository techRepository;

    @Override
    public void requireExistsEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            throw new EmployeeException(employeeId);

        }
    }

    @Override
    public void requireExistsProject(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ProjectException("Could not find project " + projectId);
        }
    }

    @Override
    public void requireExistsTech(Long techId) {
        if (!techRepository.existsById(techId))
            throw new TechException("Tech id %s not found".formatted(techId));

    }
}
