package com.org.qualitycore.standardinformation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Schema(description = "발효 상세 공정 DTO")
public class FermentationDetailsDTO {

    @Schema(description = "발효 공정 ID", example = "FER001")
    private String fermentationId;

    @Schema(description = "작업지시 ID", example = "LOT2025021201")
    private String lotNo;

    @Schema(description = "발효 소요 시간 (분)", example = "720")
    private Integer fermentationTime;

    @Schema(description = "발효 시작 온도 (°C)", example = "20.0")
    private Double startTemperature;

    @Schema(description = "최종 당도 (°Bx)", example = "2.0")
    private Double finalSugarContent;

    @Schema(description = "효모 종류", example = "에일효모")
    private String yeastType;

    @Schema(description = "효모 투입량 (g)", example = "10.0")
    private Double yeastAmount;

    @Schema(description = "초기 당도 (°Bx)", example = "15.5")
    private Double initialSugarContent;

    @Schema(description = "메모 사항", example = "작업자: 홍길동, 냉각 완료")
    private String notes;

    @Schema(description = "시작시간" , example = "2025-02-12T10:15:30")
    private LocalDateTime startTime;

    @Schema(description = "예상 종료 시간" , example = "2025-02-12T10:55:30")
    private LocalDateTime expectedEndTime;

    @Schema(description = "실제 종료 시간" , example = "2025-02-12T11:00:30")
    private LocalDateTime actualEndTime;

    @Schema(description = "공정 추적 DTO" )
    private ProcessTrackingDTONam processTracking;

}
