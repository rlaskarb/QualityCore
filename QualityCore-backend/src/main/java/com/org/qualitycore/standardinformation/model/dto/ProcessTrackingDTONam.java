package com.org.qualitycore.standardinformation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "공정 추적 DTO")
public class ProcessTrackingDTONam {

    @Schema(description = "공정 추적 ID", example = "1")
    private Long trackingId;

    @Schema(description = "작업지시 ID" , example = "LOT2025021201")
    private String lotNo;  // workOrders 대신 lotNo만 포함

    @Schema(description = "상태 코드 ID", example = "SC001")
    private String statusCode;

    @Schema(description = "공정 상태", example = "대기 중")
    private String processStatus;

    @Schema(description = "공정 이름", example = "분쇄")
    private String processName;

}
