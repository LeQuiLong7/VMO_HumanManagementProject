package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    <T> Optional<T> findById(Long id, Class<T> clazz);

    <T> Optional<T> findByEmail(String email, Class<T> clazz);

    Optional<Employee> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    boolean existsByPersonalEmail(String personalEmail);

    @Modifying
    @Transactional
    @Query("update Employee  e set e.leaveDays = e.leaveDays + 1 where e.quit = false")
    int increaseLeaveDaysBy1();
    @Modifying
    @Transactional
    @Query("update Employee  e set e.leaveDays = e.leaveDays - 1 where e.id = :employeeId")
    int decreaseLeaveDaysBy1(Long employeeId);


    @Modifying
    @Transactional
    @Query("update Employee  e set e.currentSalary = :newSalary where e.id = :employeeId")
    int updateSalaryById(Long employeeId, Double newSalary);


    int countByEmailLike(String email);
}
