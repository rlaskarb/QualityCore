package com.org.qualitycore.password.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetRequest {
    @NotBlank(message = "사번은 필수 입력값입니다.")
    private String employeeId;

    @NotBlank(message = "인증 코드는 필수 입력값입니다.")
    private String token;

    @NotBlank(message = "새 비밀번호는 필수 입력값입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
    private String newPassword;

    @NotBlank(message = "비밀번호 확인은 필수 입력값입니다.")
    private String confirmPassword;
}