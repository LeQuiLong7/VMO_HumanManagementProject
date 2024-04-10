package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.employee.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    <T> Optional<T> findById(Long id, Class<T> clazz);

    <T> Optional<T> findByEmail(String email, Class<T> clazz);

    boolean existsByEmail(String email);
    boolean existsById(Long id);
    boolean existsByPersonalEmail(String personalEmail);
    int countByEmailLike(String email);
}
