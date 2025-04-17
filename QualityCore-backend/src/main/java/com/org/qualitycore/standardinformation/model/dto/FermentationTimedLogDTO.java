package com.org.qualitycore.standardinformation.model.dto;

import com.org.qualitycore.standardinformation.model.entity.FermentationDetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Schema(description = "발효 시간대별 기록 DTO")
public class FermentationTimedLogDTO {

    @Schema(description = "발효 시간대별 기록 ID", example = "1")
    private Long flogId;

    @Schema(description = "발효 공정 ID", example = "FER001")
    private String fermentationId;

    @Schema(description = "데이터 기록 시간", example = "2")
    private Integer recordTime;

    @Schema(description = "발효 온도 (°C)", example = "22.5")
    private Double temperature;

    @Schema(description = "CO2 포집량 (L)", example = "1.2")
    private Double co2Collection;

    @Schema(description = "압력 (bar)", example = "1.1")
    private Double pressure;

    @Schema(description = "실내 습도 (%)", example = "55.0")
    private Double roomHumidity;

    @Schema(description = "실내 온도 (°C)", example = "20.0")
    private Double roomTemperature;

    @Schema(description = "알코올 농도 (%)", example = "4.5")
    private Double alcoholConcentration;

    @Schema(description = "메모 사항", example = "pH 안정적, 발효 진행 중")
    private String notes;
}
