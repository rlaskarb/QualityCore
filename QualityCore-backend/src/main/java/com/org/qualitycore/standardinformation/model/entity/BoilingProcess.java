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
@Table(name = "BOILING_PROCESS")
@Schema(description = "끓임 공정 엔티티")
public class BoilingProcess {

    @Id
    @Column(name = "BOILING_ID", nullable = false, updatable = false)
    @Schema(description = "끓임 공정 ID", example = "BO001")
    private String boilingId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "boilingProcess")
    @Schema(description = "작업지시 ID" , example ="LOT2025021301")
    private List<LineMaterial> lineMaterials;

    @ManyToOne(fetch = FetchType.LAZY )
    @Schema(description = "공정상태 체크" )
    private processTracking processTracking;

    @Column(name = "LOT_NO", nullable = false)
    @Schema(description = "작업지시 ID", example = "LOT2025021201")
    private String lotNo;

    @Column(name = "BOILING_TIME", nullable = false)
    @Schema(description = "끓임 소요 시간 (분)", example = "60.0")
    private Integer boilingTime;

    @Column(name = "TEMPERATURE", nullable = false)
    @Schema(description = "끓임 온도 (°C)", example = "100.0")
    private Double temperature;

    @Column(name = "INITIAL_WORT_VOLUME", nullable = false)
    @Schema(description = "초기 워트량 (L)", example = "3200.0")
    private Double initialWortVolume;

    @Column(name = "POST_BOIL_WORT_VOLUME")
    @Schema(description = "끓임 후 워트량 (L)", example = "3000.0")
    private Double postBoilWortVolume;

    @Column(name = "BOIL_LOSS_VOLUME")
    @Schema(description = "끓임 손실량 (L)", example = "200.0")
    private Double boilLossVolume;


    @Column(name ="FIRST_HOP_NAME")
    @Schema(description = "첫번쨰 홉 이름" , example = "마그넘 홉")
    private String firstHopName ;


    @Column(name ="FIRST_HOP_AMOUNT")
    @Schema(description = "첫번째 홉 투입량", example = "20.0")
    private Double firstHopAmount;


    @Column(name ="SECOND_HOP_NAME")
    @Schema(description = "두번쨰 홉 이름" , example = "캐스케이드 홉")
    private String secondHopName;


    @Column(name ="SECOND_HOP_AMOUNT")
    @Schema(description = "두번쨰 홉 투입량" , example = "15.0")
    private Double secondHopAmount;


    @CreationTimestamp
    @Column(name = "START_TIME", nullable = false)
    @Schema(description = "작업 시작 시간", example = "2025-02-12T14:00:00")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME", nullable = false)
    @Schema(description = "예상 종료 시간", example = "2025-02-12T15:00:00")
    private LocalDateTime expectedEndTime;

    @PreUpdate   // update 이전 자동 실행
    public void updateExpectedEndTime() {
        if (expectedEndTime == null && boilingTime != null) {
            expectedEndTime = startTime.plusMinutes(boilingTime);
        }
    }

    @UpdateTimestamp
    @Column(name = "ACTUAL_END_TIME")
    @Schema(description = "실제 종료 시간", example = "2025-02-12T15:10:00")
    private LocalDateTime actualEndTime;

    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "작업자: 홍길동, 끓임 완료")
    private String notes;

    @PrePersist
    public void prePersist() {
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }
        if (expectedEndTime == null && boilingTime != null) {
            expectedEndTime = startTime.plusMinutes(boilingTime.longValue());
        }
    }

    @Override
    public String toString() {
        return "BoilingProcess{" +
                "boilingId='" + boilingId + '\'' +
                ", lineMaterials=" + lineMaterials +
                ", processTracking=" + processTracking +
                ", lotNo='" + lotNo + '\'' +
                ", boilingTime=" + boilingTime +
                ", temperature=" + temperature +
                ", initialWortVolume=" + initialWortVolume +
                ", postBoilWortVolume=" + postBoilWortVolume +
                ", boilLossVolume=" + boilLossVolume +
                ", startTime=" + startTime +
                ", expectedEndTime=" + expectedEndTime +
                ", actualEndTime=" + actualEndTime +
                ", notes='" + notes + '\'' +
                '}';
    }
}