package com.org.qualitycore.password.model.entity;



import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "PASSWORD_RESET_TOKENS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "PASSWORD_RESET_SEQ")
    @SequenceGenerator(name = "PASSWORD_RESET_SEQ", sequenceName = "PASSWORD_RESET_SEQ", allocationSize = 1)
    private Long id;

    @Column(name = "EMPLOYEE_ID", nullable = false)
    private String employeeId;

    @Column(name = "TOKEN", nullable = false)
    private String token;

    @Column(name = "CREATED_AT")
    private LocalDateTime createdAt;

    @Column(name = "EXPIRES_AT", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "USED")
    private boolean used;

    // 토큰이 만료되었는지 확인하는 메소드
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expiresAt);
    }
}