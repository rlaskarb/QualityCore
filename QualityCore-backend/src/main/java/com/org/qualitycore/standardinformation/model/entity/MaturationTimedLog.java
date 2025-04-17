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
@Table(name = "MATURATION_TIMED_LOG")
@Schema(description = "숙성 시간대별 기록 엔티티")
public class MaturationTimedLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MLOG_ID", nullable = false, updatable = false)
    @Schema(description = "숙성 시간대별 기록 ID", example = "1")
    private Long mlogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MATURATION_ID", nullable = false)
    @Schema(description = "숙성 공정 ID", example = "MAT001")
    private MaturationDetails maturationDetails;

    @Column(name = "RECORD_TIME", nullable = false)
    @Schema(description = "데이터 기록 시간", example = "2")
    private Integer recordTime;

    @Column(name = "TEMPERATURE", nullable = false)
    @Schema(description = "숙성 온도 (°C)", example = "15.0")
    private Double temperature;

    @Column(name = "PRESSURE", nullable = false)
    @Schema(description = "압력 (bar)", example = "1.0")
    private Double pressure;

    @Column(name = "CO2_PERCENT", nullable = false)
    @Schema(description = "CO2 농도 (%)", example = "0.5")
    private Double co2Percent;

    @Column(name = "DISSOLVED_OXYGEN", nullable = false)
    @Schema(description = "용존 산소량 (ppm)", example = "0.8")
    private Double dissolvedOxygen;

    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "산소량 조절 필요")
    private String notes;

    @CreationTimestamp
    @Column(name = "START_TIME", nullable = false)
    @Schema(description = "작업 시작 시간", example = "2025-03-09T10:00:00")
    private LocalDateTime startTime;

    @UpdateTimestamp
    @Column(name = "END_TIME")
    @Schema(description = "실제 종료 시간", example = "2025-03-19T11:00:00")
    private LocalDateTime actualEndTime;

    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
    }

    @Override
    public String toString() {
        return "MaturationTimedLog{" +
                "mlogId=" + mlogId +
                ", maturationDetails=" + maturationDetails +
                ", recordTime=" + recordTime +
                ", temperature=" + temperature +
                ", pressure=" + pressure +
                ", co2Percent=" + co2Percent +
                ", dissolvedOxygen=" + dissolvedOxygen +
                ", notes='" + notes + '\'' +
                '}';
    }
}
