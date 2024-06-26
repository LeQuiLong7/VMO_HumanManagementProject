package com.lql.humanresourcedemo.repository.employee;

import com.lql.humanresourcedemo.model.employee.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    <T> Optional<T> findById(Long id, Class<T> clazz);

    <T> Optional<T> findByEmail(String email, Class<T> clazz);
    <T> Optional<T> findByPersonalEmail(String personalEmail, Class<T> clazz);

    Optional<Employee> findByEmail(String email);
    @Query("select e.id from Employee e where e.managedBy.id = :managedBy or e.id = :managedBy ")
    List<Long> findAllIdByManagedById(Long managedBy);

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
    @Query("update Employee  e set e.currentEffort =  (e.currentEffort + :effort) where e.id = :employeeId")
    void updateCurrentEffortById(Long employeeId, Integer effort);

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
