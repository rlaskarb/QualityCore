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
@Table(name = "FERMENTATION_DETAILS")
@Schema(description = "발효 상세 공정 엔티티")
public class FermentationDetails {

    @Id
    @Column(name = "FERMENTATION_ID", nullable = false, updatable = false)
    @Schema(description = "발효 공정 ID", example = "FER001")
    private String fermentationId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "fermentationDetails")
    @Schema(description = "작업지시 ID", example = "LOT2025021301")
    private List<LineMaterial> lineMaterials;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(description = "공정 상태 체크")
    private processTracking processTracking;

    @Column(name = "LOT_NO", nullable = false)
    @Schema(description = "작업지시 ID", example = "LOT2025021201")
    private String lotNo;

    @Column(name = "FERMENTATION_TIME", nullable = false)
    @Schema(description = "발효 소요 시간 (분)", example = "720")
    private Integer fermentationTime;

    @Column(name = "START_TEMPERATURE", nullable = false)
    @Schema(description = "발효 시작 온도 (°C)", example = "20.0")
    private Double startTemperature;

    @Column(name = "INITIAL_SUGAR_CONTENT", nullable = false)
    @Schema(description = "초기 당도 (°Bx)", example = "15.5")
    private Double initialSugarContent;

    @Column(name = "FINAL_SUGAR_CONTENT")
    @Schema(description = "최종 당도 (°Bx)", example = "2.0")
    private Double finalSugarContent;

    @Column(name = "YEAST_TYPE", nullable = false)
    @Schema(description = "효모 종류", example = "에일효모 ")
    private String yeastType;

    @Column(name = "YEAST_AMOUNT", nullable = false)
    @Schema(description = "효모 투입량 (g)", example = "10.0")
    private Double yeastAmount;

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

    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "발효 완료, pH 안정적")
    private String notes;

    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (expectedEndTime == null && fermentationTime != null) {
            expectedEndTime = startTime.plusMinutes(fermentationTime.longValue());
        }
    }

    @Override
    public String toString() {
        return "FermentationDetails{" +
                "fermentationId='" + fermentationId + '\'' +
                ", lineMaterials=" + lineMaterials +
                ", processTracking=" + processTracking +
                ", lotNo='" + lotNo + '\'' +
                ", fermentationTime=" + fermentationTime +
                ", startTemperature=" + startTemperature +
                ", initialSugarContent=" + initialSugarContent +
                ", finalSugarContent=" + finalSugarContent +
                ", yeastType='" + yeastType + '\'' +
                ", yeastAmount=" + yeastAmount +
                ", startTime=" + startTime +
                ", expectedEndTime=" + expectedEndTime +
                ", actualEndTime=" + actualEndTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}

