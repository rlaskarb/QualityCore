package com.org.qualitycore.standardinformation.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "API 응답 메시지 객체")
public class ErpMessage {

    @Schema(description = "HTTP 상태 코드", example = "200")
    private int httpStatusCode;

    @Schema(description = "응답 메시지", example = "요청이 성공적으로 처리되었습니다.")
    private String message;
}