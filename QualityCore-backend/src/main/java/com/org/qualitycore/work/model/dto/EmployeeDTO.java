package com.org.qualitycore.work.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class EmployeeDTO {

    @Schema(description = "사원코드")
    private String empId;

    @Schema(description = "비밀번호")
    private String passWord;

    @Schema(description = "사원이름")
    private String empName;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "휴대폰")
    private String phone;

    @Schema(description = "사용자 권한")
    private String authority;

    @Schema(description = "프로필사진")
    private String profileImage;

    @Schema(description = "작업조")
    private String workTeam;

}
