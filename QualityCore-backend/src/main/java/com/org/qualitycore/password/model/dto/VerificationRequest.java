package com.org.qualitycore.password.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerificationRequest {
    @NotBlank(message = "사번은 필수 입력값입니다.")
    private String employeeId;
}