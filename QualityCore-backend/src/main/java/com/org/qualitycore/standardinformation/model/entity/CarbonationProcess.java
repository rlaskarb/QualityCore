package com.org.qualitycore.standardinformation.model.entity;

import com.org.qualitycore.work.model.entity.LineMaterial;
import com.org.qualitycore.work.model.entity.processTracking;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "CARBONATION_PROCESS")
@Schema(description = "탄산 조정 공정 엔티티")
public class CarbonationProcess {

    @Id
    @Column(name = "CARBONATION_ID", nullable = false, updatable = false)
    @Schema(description = "탄산 조정 공정 고유 ID", example = "CA001")
    private String carbonationId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "carbonationProcess")
    @Schema(description = "작업지시 ID" , example ="LOT2025021301")
    private List<LineMaterial> lineMaterials;

    @ManyToOne(fetch = FetchType.LAZY )
    @Schema(description = "공정상태 체크" )
    private processTracking processTracking;

    @Column(name = "LOT_NO", nullable = false)
    @Schema(description = "작업지시 ID", example = "LOT2025030801")
    private String lotNo;

    @Column(name = "CARBONATION_TIME", nullable = false)
    @Schema(description = "탄산 조정 소요 시간 (분)", example = "30")
    private Integer carbonationTime;

    @Column(name = "CO2_CARBONATION_PERCENT")
    @Schema(description = "CO2 농도 (%)", example = "2.5")
    private Double co2CarbonationPercent;

    @Column(name = "PROCESS_TEMPERATURE", nullable = false)
    @Schema(description = "탄산 공정 온도 (°C)", example = "4.0")
    private Double processTemperature;

    @Column(name = "PROCESS_PRESSURE", nullable = false)
    @Schema(description = "공정 중 압력 (bar)", example = "2.0")
    private Double processPressure;

    @CreationTimestamp
    @Column(name = "START_TIME", nullable = false)
    @Schema(description = "작업 시작 시간", example = "2025-03-08T10:00:00")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME", nullable = false)
    @Schema(description = "예상 종료 시간", example = "2025-03-08T10:30:00")
    private LocalDateTime expectedEndTime;

    @UpdateTimestamp
    @Column(name = "ACTUAL_END_TIME")
    @Schema(description = "실제 종료 시간", example = "2025-03-08T10:35:00")
    private LocalDateTime actualEndTime;

    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "탄산 조정 완료, 추가 검사 필요")
    private String notes;

    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (expectedEndTime == null && carbonationTime != null) {
            expectedEndTime = startTime.plusMinutes(carbonationTime.longValue());
        }
    }

    @Override
    public String toString() {
        return "CarbonationProcess{" +
                "carbonationId='" + carbonationId + '\'' +
                ", lotNo='" + lotNo + '\'' +
                ", carbonationTime=" + carbonationTime +
                ", co2CarbonationPercent=" + co2CarbonationPercent +
                ", processTemperature=" + processTemperature +
                ", processPressure=" + processPressure +
                ", startTime=" + startTime +
                ", expectedEndTime=" + expectedEndTime +
                ", actualEndTime=" + actualEndTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}
