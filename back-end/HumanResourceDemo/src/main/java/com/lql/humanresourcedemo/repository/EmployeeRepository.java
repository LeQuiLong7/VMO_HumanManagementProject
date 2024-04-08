package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findByEmail(String email);

    <T> Optional<T> findByEmail(String email, Class<T> clazz);
}
