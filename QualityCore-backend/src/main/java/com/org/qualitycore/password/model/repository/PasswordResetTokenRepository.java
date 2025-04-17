package com.org.qualitycore.password.model.repository;

import com.org.qualitycore.password.model.entity.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    @Query("SELECT t FROM PasswordResetToken t WHERE t.employeeId = :employeeId AND t.token = :token AND t.used = false AND t.expiresAt > CURRENT_TIMESTAMP")
    Optional<PasswordResetToken> findValidToken(@Param("employeeId") String employeeId, @Param("token") String token);

    @Query("SELECT t FROM PasswordResetToken t WHERE t.employeeId = :employeeId ORDER BY t.createdAt DESC")
    Optional<PasswordResetToken> findLatestTokenByEmployeeId(@Param("employeeId") String employeeId);
}