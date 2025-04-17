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
@Table(name = "COOLING_PROCESS")
@Schema(description = "냉각 공정 엔티티")
public class CoolingProcess {

    @Id
    @Column(name = "COOLING_ID", nullable = false, updatable = false)
    @Schema(description = "냉각 공정 ID", example = "CO001")
    private String coolingId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "coolingProcess")
    @Schema(description = "작업지시 ID", example = "LOT2025021301")
    private List<LineMaterial> lineMaterials;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(description = "공정 상태 체크")
    private processTracking processTracking;

    @Column(name = "LOT_NO", nullable = false)
    @Schema(description = "작업지시 ID", example = "LOT2025021201")
    private String lotNo;

    @Column(name = "COOLING_TIME", nullable = false)
    @Schema(description = "냉각 소요 시간 (분)", example = "45.0")
    private Integer coolingTime;

    @Column(name = "TARGET_TEMPERATURE", nullable = false)
    @Schema(description = "냉각 목표 온도 (°C)", example = "5.0")
    private Double targetTemperature;

    @Column(name = "COOLANT_TEMPERATURE", nullable = false)
    @Schema(description = "냉각수 온도 (°C)", example = "2.0")
    private Double coolantTemperature;

    @CreationTimestamp
    @Column(name = "START_TIME", nullable = false)
    @Schema(description = "작업 시작 시간", example = "2025-02-12T16:00:00")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME", nullable = false)
    @Schema(description = "예상 종료 시간", example = "2025-02-12T16:45:00")
    private LocalDateTime expectedEndTime;

    @PreUpdate
    public void updateExpectedEndTime() {
        if (expectedEndTime == null && coolingTime != null) {
            expectedEndTime = startTime.plusMinutes(coolingTime);
        }
    }

    @UpdateTimestamp
    @Column(name = "ACTUAL_END_TIME")
    @Schema(description = "실제 종료 시간", example = "2025-02-12T16:50:00")
    private LocalDateTime actualEndTime;

    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "작업자: 홍길동, 냉각 완료")
    private String notes;

    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (expectedEndTime == null && coolingTime != null) {
            expectedEndTime = startTime.plusMinutes(coolingTime.longValue());
        }
    }

    @Override
    public String toString() {
        return "CoolingProcess{" +
                "coolingId='" + coolingId + '\'' +
                ", lineMaterials=" + lineMaterials +
                ", processTracking=" + processTracking +
                ", lotNo='" + lotNo + '\'' +
                ", coolingTime=" + coolingTime +
                ", targetTemperature=" + targetTemperature +
                ", coolantTemperature=" + coolantTemperature +
                ", startTime=" + startTime +
                ", expectedEndTime=" + expectedEndTime +
                ", actualEndTime=" + actualEndTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}
