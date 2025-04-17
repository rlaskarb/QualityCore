package com.org.qualitycore.standardinformation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@Builder
@Schema(description = "숙성 시간대별 기록 DTO")
public class MaturationTimedLogDTO {

    @Schema(description = "숙성 시간대별 기록 ID", example = "1")
    private Long mlogId;

    @Schema(description = "숙성 공정 ID", example = "MAR001")
    private String maturationId;

    @Schema(description = "데이터 기록 시간", example = "2")
    private Integer recordTime;

    @Schema(description = "숙성 온도 (°C)", example = "15.0")
    private Double temperature;

    @Schema(description = "압력 (bar)", example = "1.0")
    private Double pressure;

    @Schema(description = "CO2 농도 (%)", example = "0.5")
    private Double co2Percent;

    @Schema(description = "용존 산소량 (ppm)", example = "0.8")
    private Double dissolvedOxygen;

    @Schema(description = "메모 사항", example = "산소량 조절 필요")
    private String notes;

    @Schema(description = "공정 추적 DTO" )
    private ProcessTrackingDTONam processTracking;
}
