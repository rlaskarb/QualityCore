package com.org.qualitycore.password.controller;

import com.org.qualitycore.password.model.dto.PasswordResetRequest;
import com.org.qualitycore.password.model.dto.TokenVerificationRequest;
import com.org.qualitycore.password.model.dto.VerificationRequest;
import com.org.qualitycore.password.service.PasswordResetService;
import com.org.qualitycore.password.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/password-reset")

public class PasswordResetController {

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;

    /**
     * 비밀번호 재설정을 위한 인증 코드 발송 요청
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@Valid @RequestBody VerificationRequest request) {
        // 이메일 주소 확인
        Optional<String> emailOpt = userService.findEmailByEmployeeId(request.getEmployeeId());

        if (emailOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "등록된 사번이 없습니다."));
        }

        // 인증 코드 생성 및 이메일 발송
        String code = passwordResetService.createPasswordResetToken(request.getEmployeeId());

        if (code == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "인증 코드 생성에 실패했습니다."));
        }

        // 이메일 마스킹 처리 (앞 2자리 제외 마스킹)
        String email = emailOpt.get();
        String maskedEmail = maskEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "인증 코드가 이메일로 발송되었습니다.");
        response.put("email", maskedEmail);

        return ResponseEntity.ok(response);
    }

    /**
     * 인증 코드 확인 요청
     */
    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@Valid @RequestBody TokenVerificationRequest request) {
        boolean isValid = passwordResetService.verifyToken(request.getEmployeeId(), request.getToken());

        if (!isValid) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "인증 코드가 유효하지 않습니다."));
        }

        return ResponseEntity.ok(Map.of("success", true, "message", "인증이 완료되었습니다."));
    }

    /**
     * 비밀번호 재설정 요청
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetRequest request) {
        // 비밀번호 유효성 검사
        if (request.getNewPassword().length() < 4) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "비밀번호는 최소 4자 이상이어야 합니다."
            ));
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "비밀번호가 일치하지 않습니다."
            ));
        }

        // 비밀번호 재설정
        boolean success = passwordResetService.resetPassword(
                request.getEmployeeId(),
                request.getToken(),
                request.getNewPassword()
        );

        if (!success) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "비밀번호 재설정에 실패했습니다. 인증 코드를 확인해주세요."
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "비밀번호가 성공적으로 변경되었습니다. 새 비밀번호로 로그인해주세요."
        ));
    }

    /**
     * 이메일 주소 마스킹 처리 (앞 2자리 제외 마스킹)
     */
    private String maskEmail(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex <= 2) {
            // @ 앞 부분이 2자 이하인 경우
            return "*" + email.substring(atIndex);
        }

        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex);

        String maskedUsername = username.substring(0, 2) + "*".repeat(username.length() - 2);

        return maskedUsername + domain;
    }
}