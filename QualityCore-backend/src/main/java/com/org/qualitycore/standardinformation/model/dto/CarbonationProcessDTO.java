package com.org.qualitycore.standardinformation.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Schema(description = "탄산 조정 공정 DTO")
public class CarbonationProcessDTO {

    @Schema(description = "탄산 조정 공정 고유 ID", example = "CA001")
    private String carbonationId;

    @Schema(description = "작업지시 ID", example = "LOT2025030801")
    private String lotNo;

    @Schema(description = "탄산 조정 소요 시간 (분)", example = "30")
    private Integer carbonationTime;

    @Schema(description = "CO2 농도 (%)", example = "2.5")
    private Double co2CarbonationPercent;

    @Schema(description = "탄산 공정 온도 (°C)", example = "4.0")
    private Double processTemperature;

    @Schema(description = "공정 중 압력 (bar)", example = "2.0")
    private Double processPressure;

    @Schema(description = "작업 시작 시간", example = "2025-03-08T10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "예상 종료 시간", example = "2025-03-08T10:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expectedEndTime;

    @Schema(description = "실제 종료 시간", example = "2025-03-08T10:35:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualEndTime;

    @Schema(description = "메모 사항", example = "탄산 조정 완료, 추가 검사 필요")
    private String notes;

    @Schema(description = "공정 추적 DTO" )
    private ProcessTrackingDTONam processTracking;
}
