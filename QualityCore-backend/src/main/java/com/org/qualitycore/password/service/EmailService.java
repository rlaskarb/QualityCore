package com.org.qualitycore.password.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * 비밀번호 재설정 인증 코드를 포함한 이메일을 발송합니다.
     *
     * @param to 수신자 이메일
     * @param employeeName 사원 이름
     * @param verificationCode 인증 코드
     * @return 발송 성공 여부
     */
    public boolean sendPasswordResetEmail(String to, String employeeName, String verificationCode) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            // 이메일 템플릿에 전달할 데이터
            Context context = new Context();
            context.setVariable("employeeName", employeeName != null ? employeeName : "사용자");
            context.setVariable("verificationCode", verificationCode);

            // Thymeleaf 템플릿 처리
            String htmlContent = templateEngine.process("password-reset-email", context);

            // 이메일 메시지 설정
            helper.setTo(to);
            helper.setSubject("[BräuHaus] 비밀번호 재설정 인증 코드");
            helper.setText(htmlContent, true);
            helper.setFrom("no-reply@brauhaus.com");

            // 이메일 발송
            mailSender.send(message);
            return true;
        } catch (MessagingException e) {
            // 로깅
            System.err.println("이메일 발송 실패: " + e.getMessage());
            return false;
        }
    }
}