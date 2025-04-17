package com.org.qualitycore.password.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenVerificationRequest {
    @NotBlank(message = "사번은 필수 입력값입니다.")
    private String employeeId;

    @NotBlank(message = "인증 코드는 필수 입력값입니다.")
    @Size(min = 6, max = 6, message = "인증 코드는 6자리여야 합니다.")
    private String token;
}