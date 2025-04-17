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
@Table(name = "POST_MATURATION_FILTRATION")
@Schema(description = "숙성 후 여과 공정 엔티티")
public class PostMaturationFiltration {

    @Id
    @Column(name = "MFILTRATION_ID", nullable = false, updatable = false)
    @Schema(description = "여과 공정 고유 ID", example = "FIL001")
    private String mfiltrationId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "postMaturationFiltration")
    @Schema(description = "작업지시 ID" , example ="LOT2025021301")
    private List<LineMaterial> lineMaterials;

    @ManyToOne(fetch = FetchType.LAZY )
    @Schema(description = "공정상태 체크" )
    private processTracking processTracking;

    @Column(name = "LOT_NO", nullable = false)
    @Schema(description = "작업지시 ID", example = "LOT2025030801")
    private String lotNo;

    @Column(name = "FILTRATION_TIME", nullable = false)
    @Schema(description = "여과 소요 시간", example = "2")
    private Integer filtrationTime;

    @Column(name = "TURBIDITY")
    @Schema(description = "탁도 (NTU 단위)", example = "1.5")
    private Double turbidity;

    @CreationTimestamp
    @Column(name = "START_TIME", nullable = false)
    @Schema(description = "작업 시작 시간", example = "2025-03-08T10:00:00")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME", nullable = false)
    @Schema(description = "예상 종료 시간", example = "2025-03-08T12:00:00")
    private LocalDateTime expectedEndTime;

    @UpdateTimestamp
    @Column(name = "ACTUAL_END_TIME")
    @Schema(description = "실제 종료 시간", example = "2025-03-08T12:30:00")
    private LocalDateTime actualEndTime;

    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "여과 완료, 품질 점검 필요")
    private String notes;

    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (expectedEndTime == null && filtrationTime != null) {
            expectedEndTime = startTime.plusMinutes(filtrationTime.longValue());
        }
    }

    @Override
    public String toString() {
        return "PostMaturationFiltration{" +
                "mfiltrationId='" + mfiltrationId + '\'' +
                ", lotNo='" + lotNo + '\'' +
                ", filtrationTime=" + filtrationTime +
                ", turbidity=" + turbidity +
                ", startTime=" + startTime +
                ", expectedEndTime=" + expectedEndTime +
                ", actualEndTime=" + actualEndTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}