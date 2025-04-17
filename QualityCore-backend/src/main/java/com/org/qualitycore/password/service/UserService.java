package com.org.qualitycore.password.service;

import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    // 실제 구현에서는 DB에서 사용자 정보를 조회
    // 현재는 Mock 데이터 사용
    private final Map<String, String> userEmails = new HashMap<>();

    public UserService() {
        // 초기 데이터 설정 - 실제 구현에서는 DB에서 조회
        userEmails.put("admin", "answjdgus72@gmail.com");
        userEmails.put("plan", "answjdgus72@gmail.com");
        userEmails.put("work", "answjdgus72@gmail.com");
        userEmails.put("EMP001", "answjdgus72@gmail.com");
        userEmails.put("iu", "answjdgus72@gmail.com");
    }

    /**
     * 사번으로 사용자의 이메일을 조회합니다.
     *
     * @param employeeId 사번
     * @return 이메일 주소 (존재하지 않으면 빈 Optional)
     */
    public Optional<String> findEmailByEmployeeId(String employeeId) {
        return Optional.ofNullable(userEmails.get(employeeId));
    }

    /**
     * 사용자 비밀번호를 업데이트합니다.
     *
     * @param employeeId 사번
     * @param newPassword 새 비밀번호
     * @return 성공 여부
     */
    public boolean updatePassword(String employeeId, String newPassword) {
        // 실제 구현에서는 DB에 비밀번호를 저장
        // 여기서는 항상 성공으로 처리
        return userEmails.containsKey(employeeId);
    }
}