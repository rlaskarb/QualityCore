package com.org.qualitycore.password.service;


import com.org.qualitycore.password.model.entity.PasswordResetToken;
import com.org.qualitycore.password.model.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PasswordResetService {

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    // 인증 코드 유효 시간 (분)
    private static final int TOKEN_EXPIRY_MINUTES = 10;

    /**
     * 비밀번호 재설정 요청 처리
     *
     * @param employeeId 사번
     * @return 생성된 인증 코드 또는 null (사용자가 존재하지 않는 경우)
     */
    @Transactional
    public String createPasswordResetToken(String employeeId) {
        // 사번으로 이메일 조회
        Optional<String> emailOpt = userService.findEmailByEmployeeId(employeeId);

        if (emailOpt.isEmpty()) {
            return null; // 사용자가 존재하지 않음
        }

        // 인증 코드 생성 (6자리 숫자)
        String verificationCode = generateVerificationCode();

        // 토큰 엔티티 생성
        PasswordResetToken token = PasswordResetToken.builder()
                .employeeId(employeeId)
                .token(verificationCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES))
                .used(false)
                .build();

        // 저장
        tokenRepository.save(token);

        // 이메일 발송
        emailService.sendPasswordResetEmail(emailOpt.get(), employeeId, verificationCode);

        return verificationCode;
    }

    /**
     * 인증 코드 검증
     *
     * @param employeeId 사번
     * @param token 인증 코드
     * @return 검증 성공 여부
     */
    @Transactional
    public boolean verifyToken(String employeeId, String token) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findValidToken(employeeId, token);

        if (tokenOpt.isEmpty()) {
            return false; // 유효한 토큰이 없음
        }

        return true;
    }

    /**
     * 비밀번호 재설정
     *
     * @param employeeId 사번
     * @param token 인증 코드
     * @param newPassword 새 비밀번호
     * @return 성공 여부
     */
    @Transactional
    public boolean resetPassword(String employeeId, String token, String newPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findValidToken(employeeId, token);

        if (tokenOpt.isEmpty()) {
            return false; // 유효한 토큰이 없음
        }

        // 토큰 사용 처리
        PasswordResetToken resetToken = tokenOpt.get();
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        // 비밀번호 업데이트
        return userService.updatePassword(employeeId, newPassword);
    }

    /**
     * 6자리 랜덤 인증 코드 생성
     *
     * @return 6자리 숫자 문자열
     */
    private String generateVerificationCode() {
        SecureRandom random = new SecureRandom();
        int code = 100000 + random.nextInt(900000); // 100000-999999 범위의 숫자
        return String.valueOf(code);
    }
}
