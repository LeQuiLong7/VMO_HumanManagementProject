package com.lql.humanresourcedemo.repository;

import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    <T> Optional<T> findById(Long id, Class<T> clazz);

    <T> Optional<T> findByEmail(String email, Class<T> clazz);

    Optional<Employee> findByEmail(String email);

    Page<Employee> findAllByManagedById(Long manageBy, Pageable pageable);

    <T> List<T> findByQuitIsFalse(Class<T> clazz);

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
    void decreaseLeaveDaysBy1(Long employeeId);


    @Modifying
    @Transactional
    @Query("update Employee  e set e.currentSalary = :newSalary where e.id = :employeeId")
    void updateSalaryById(Long employeeId, Double newSalary);

    @Modifying
    @Transactional
    @Query("update Employee e set e.avatarUrl = :avatarUrl where e.id = :employeeId")
    void updateAvatarURLById(Long employeeId, String avatarUrl);


    @Modifying
    @Transactional
    @Query("update Employee e set e.password = :newPassword where e.id = :employeeId")
    void updatePasswordById(Long employeeId, String newPassword);
    int countByEmailLike(String email);
}
