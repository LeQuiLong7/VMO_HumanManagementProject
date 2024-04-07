package com.lql.humanresourcedemo.service;

import com.lql.humanresourcedemo.model.employee.Employee;
import com.lql.humanresourcedemo.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepository employeeRepository;


    public Optional<Employee> findById(Long id) {
        return employeeRepository.findById(id);
    }

}
