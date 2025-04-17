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
@Builder(toBuilder = true)
@Entity
@Table(name = "FILTRATION_PROCESS")
@Schema(description = "여과 공정 엔티티")
public class FiltrationProcess {


    @Id
    @Column(name = "FILTRATION_ID", nullable = false , updatable = false  )
    @Schema(description = "여과 공정 ID", example = "FI001")
    private String filtrationId;

    @Column(name = "LOT_NO", nullable = false)
    @Schema(description = "작업지시 ID", example = "LOT2025021201")
    private String lotNo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "filtrationProcess")
    @Schema(description = "작업지시 ID" , example ="LOT2025021301")
    private List<LineMaterial> lineMaterials;

    @ManyToOne(fetch = FetchType.LAZY )
    @Schema(description = "공정상태 체크" )
    private processTracking processTracking;

    @Column(name = "FILTRATION_TIME", nullable = false)
    @Schema(description = "여과 소요 시간 (분)", example = "45.5")
    private Integer filtrationTime;

    @Column(name = "GRAIN_ABSORPTION", nullable = false)
    @Schema(description = "곡물 흡수량 (L)", example = "50.0")
    private Double grainAbsorption;

    @Column(name = "RECOVERED_WORT_VOLUME")
    @Schema(description = "회수된 워트량 (L)", example = "3100.0")
    private Double recoveredWortVolume;

    @Column(name = "LOSS_VOLUME")
    @Schema(description = "손실량 (L)", example = "10.0")
    private Double lossVolume;

    @CreationTimestamp // insert 시 자동으로 sysdate 값 저장
    @Column(name = "START_TIME", nullable = false)
    @Schema(description = "작업 시작 시간", example = "2025-02-12T11:15:30")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME", nullable = false)
    @Schema(description = "예상 종료 시간", example = "2025-02-12T12:00:00")
    private LocalDateTime expectedEndTime;


    @PreUpdate   // update 이전 자동 실행
    public void updateExpectedEndTime() {
        if (expectedEndTime == null && filtrationTime != null) {
            expectedEndTime = startTime.plusMinutes(filtrationTime);
        }
    }

    @UpdateTimestamp
    @Column(name = "ACTUAL_END_TIME")
    @Schema(description = "실제 종료 시간", example = "2025-02-12T12:05:30")
    private LocalDateTime actualEndTime;


    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "작업자: 강동원, 여과 완료")
    private String notes;



    @PrePersist
    public void prePersist() {
        // 시작 시간이 없으면 현재 시간으로 설정
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }

        // 소요 시간이 있고, 예상 종료 시간이 설정되지 않았다면 계산
        if (expectedEndTime == null && filtrationTime != null) {
            expectedEndTime = startTime.plusMinutes(filtrationTime);
        }

    }

    @Override
    public String toString() {
        return "FiltrationProcess{" +
                "filtrationId='" + filtrationId + '\'' +
                ", lotNo='" + lotNo + '\'' +
                ", lineMaterials=" + lineMaterials +
                ", processTracking=" + processTracking +
                ", filtrationTime=" + filtrationTime +
                ", grainAbsorption=" + grainAbsorption +
                ", recoveredWortVolume=" + recoveredWortVolume +
                ", lossVolume=" + lossVolume +
                ", startTime=" + startTime +
                ", expectedEndTime=" + expectedEndTime +
                ", actualEndTime=" + actualEndTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}
