package com.org.qualitycore.standardinformation.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Schema(description = "냉각 공정 DTO")
public class CoolingProcessDTO {

    @Schema(description = "냉각 공정 ID", example = "CO001")
    private String coolingId;

    @Schema(description = "작업지시 ID", example = "LOT2025021201")
    private String lotNo;

    @Schema(description = "냉각 소요 시간 (분)", example = "45.0")
    private Integer coolingTime;

    @Schema(description = "냉각 목표 온도 (°C)", example = "5.0")
    private Double targetTemperature;

    @Schema(description = "냉각수 온도 (°C)", example = "2.0")
    private Double coolantTemperature;

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
