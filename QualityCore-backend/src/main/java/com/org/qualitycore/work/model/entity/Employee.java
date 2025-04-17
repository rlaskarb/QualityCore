package com.org.qualitycore.work.model.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Entity
@Table(name = "EMPLOYEE")
public class Employee {

    @Id
    @Column(name = "EMP_ID")
    private String empId; // 사원코드

    @Column(name = "PASSWORD")
    private String passWord; // 비밀번호

    @Column(name = "EMP_NAME")
    private String empName; // 사원이름

    @Column(name = "EMAIL")
    private String email; // 이메일

    @Column(name = "PHONE")
    private String phone; // 휴대폰

    @Column(name = "AUTHORITY")
    private String authority; // 사용자 권한

    @Column(name = "PROFILE_IMAGE")
    private String profileImage; // 프로필사진

    @Column(name = "WORK_TEAM")
    private String workTeam; // 작업조

}
