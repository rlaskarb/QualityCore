package com.org.qualitycore.standardinformation.model.entity;

import com.org.qualitycore.work.model.entity.processTracking;
import com.org.qualitycore.work.model.entity.LineMaterial;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name="MATERIAL_GRINDING")
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "분쇄 공정 엔티티")
public class MaterialGrinding {

    @Id
    @Column(name = "GRINDING_ID" , nullable = false , updatable = false )
    @Schema(description = "분쇄공정 ID" , example = "GR001")
    private String grindingId;

    @Column(name = "LOT_NO" , nullable = false)
    @Schema(description = "작업지시 ID" , example = "LOT2025021201")
    private String lotNo;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "materialGrinding")
    @Schema(description = "작업지시 ID" , example ="LOT2025021301")
    private List<LineMaterial> lineMaterials;

    @ManyToOne(fetch = FetchType.LAZY )
    @Schema(description = "공정상태 체크" )
    private processTracking processTracking;

    @Column(name ="MAIN_MATERIAL" , nullable = false )
    @Schema(description = "주원료" , example ="쌀")
    private String mainMaterial ;

    @Column(name = "MAIN_MATERIAL_INPUT_VOLUME" , nullable = false)
    @Schema(description = "주원료 투입량" , example = "50.00")
    private double mainMaterialInputVolume;


    @Column(name ="MALT_TYPE" , nullable = false  )
    @Schema(description = "맥아 종류" , example = "필스너 몰트" )
    private String maltType;

    @Column(name = "MALT_INPUT_VOLUME", nullable = false)
    @Schema(description ="맥아 투입량" , example = "450")
    private Double maltInputVolume;

    @Column(name = "GRIND_INTERVAL_SETTING", nullable = false)
    @Schema(description = "분쇄 간격 설정" , example = "1.00" )
    private Double grindIntervalSetting;

    @Column(name = "GRIND_SPEED_SETTING", nullable = false)
    @Schema(description = "분쇄 속도 설정" , example = "150.00" )
    private Double grindSpeedSetting;

    @Column(name = "GRIND_DURATION", nullable = false)
    @Schema(description = "소요시간 " , example = "40" )
    private Integer grindDuration;


    @Column(name = "NOTES")
    @Schema(description = "메모사항" , example = "작업자 : 강동원  작업완료" )
    private String notes;

    @CreationTimestamp // insert 시 자동으로 sysdate 값 저장
    @Column(name = "START_TIME" , nullable = false , updatable = false)
    @Schema(description = "시작시간" , example = "2025-02-12T10:15:30")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME" , nullable = false)
    @Schema(description = "예상 종료 시간" , example = "2025-02-12T10:55:30")
    private LocalDateTime expectedEndTime;


    @PreUpdate   // update 이전 자동 실행
    public void updateExpectedEndTime() {
        if (expectedEndTime == null && grindDuration != null) {
            expectedEndTime = startTime.plusMinutes(grindDuration);
        }
    }

    @UpdateTimestamp
    @Column(name = "ACTUAL_END_TIME")
    @Schema(description = "실제 종료 시간" , example = "2025-02-12T11:00:30")
    private LocalDateTime actualEndTime;


    @PrePersist
    public void prePersist() {
        // 시작 시간이 없으면 현재 시간으로 설정
        if (startTime == null) {
            startTime = LocalDateTime.now();
        }

        // 소요 시간이 있고, 예상 종료 시간이 설정되지 않았다면 계산
        if (expectedEndTime == null && grindDuration != null) {
            expectedEndTime = startTime.plusMinutes(grindDuration);
        }


    }

    @Override
    public String toString() {
        return "MaterialGrinding{" +
                "grindingId='" + grindingId + '\'' +
                ", lotNo='" + lotNo + '\'' +
                ", lineMaterials=" + lineMaterials +
                ", mainMaterial='" + mainMaterial + '\'' +
                ", mainMaterialInputVolume=" + mainMaterialInputVolume +
                ", maltType='" + maltType + '\'' +
                ", maltInputVolume=" + maltInputVolume +
                ", grindIntervalSetting=" + grindIntervalSetting +
                ", grindSpeedSetting=" + grindSpeedSetting +
                ", grindDuration=" + grindDuration +
                ", notes='" + notes + '\'' +
                ", startTime=" + startTime +
                ", expectedEndTime=" + expectedEndTime +
                ", actualEndTime=" + actualEndTime +
                '}';
    }
}



