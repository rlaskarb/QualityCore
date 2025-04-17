package com.org.qualitycore.standardinformation.model.entity;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "FERMENTATION_TIMED_LOG")
@Schema(description = "발효 시간대별 기록 엔티티")
public class FermentationTimedLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FLOG_ID", nullable = false, updatable = false)
    @Schema(description = "발효 시간대별 기록 ID", example = "1")
    private Long flogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FERMENTATION_ID", nullable = false)
    @Schema(description = "발효 공정 ID", example = "FER001")
    private FermentationDetails fermentationDetails;


    @Column(name = "RECORD_TIME", nullable = false)
    @Schema(description = "데이터 기록 시간", example = "2")
    private Integer recordTime;

    @Column(name = "TEMPERATURE", nullable = false)
    @Schema(description = "발효 온도 (°C)", example = "22.5")
    private Double temperature;

    @Column(name = "CO2_COLLECTION", nullable = false)
    @Schema(description = "CO2 포집량 (L)", example = "1.2")
    private Double co2Collection;

    @Column(name = "PRESSURE", nullable = false)
    @Schema(description = "압력 (bar)", example = "1.1")
    private Double pressure;

    @Column(name = "ROOM_HUMIDITY", nullable = false)
    @Schema(description = "실내 습도 (%)", example = "55.0")
    private Double roomHumidity;

    @Column(name = "ROOM_TEMPERATURE", nullable = false)
    @Schema(description = "실내 온도 (°C)", example = "20.0")
    private Double roomTemperature;

    @Column(name = "ALCOHOL_CONCENTRATION", nullable = false)
    @Schema(description = "알코올 농도 (%)", example = "4.5")
    private Double alcoholConcentration;

    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "pH 안정적, 발효 진행 중")
    private String notes;

    @CreationTimestamp
    @Column(name = "START_TIME", nullable = false)
    @Schema(description = "작업 시작 시간", example = "2025-02-12T16:00:00")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME", nullable = false)
    @Schema(description = "예상 종료 시간", example = "2025-02-17T16:00:00")
    private LocalDateTime expectedEndTime;

    @UpdateTimestamp
    @Column(name = "ACTUAL_END_TIME")
    @Schema(description = "실제 종료 시간", example = "2025-02-17T17:00:00")
    private LocalDateTime actualEndTime;

    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (expectedEndTime == null && recordTime != null) {
            expectedEndTime = startTime.plusMinutes(recordTime.longValue());
        }
    }

    @Override
    public String toString() {
        return "FermentationTimedLog{" +
                "flogId=" + flogId +
                ", fermentationDetails=" + fermentationDetails +
                ", recordTime=" + recordTime +
                ", temperature=" + temperature +
                ", co2Collection=" + co2Collection +
                ", pressure=" + pressure +
                ", roomHumidity=" + roomHumidity +
                ", roomTemperature=" + roomTemperature +
                ", alcoholConcentration=" + alcoholConcentration +
                ", notes='" + notes + '\'' +
                '}';
    }
}
