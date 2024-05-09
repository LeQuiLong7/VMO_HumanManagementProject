package com.lql.humanresourcedemo.repository.passwordreset;

import com.lql.humanresourcedemo.model.password.PasswordResetRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PasswordResetRepository extends JpaRepository<PasswordResetRequest, PasswordResetRequest.PasswordResetRequestId>, JpaSpecificationExecutor<PasswordResetRequest> {


    @Query(value = "select a from PasswordResetRequest  a where a.id.token = ?1")
    Optional<PasswordResetRequest> findByToken(String token);

    @Query(value = "delete from PasswordResetRequest  a where a.id.employeeId = ?1")
    @Modifying
    void deleteByEmployeeId(Long employeeId );
}
