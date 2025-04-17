package com.org.qualitycore.routing.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WortVolumeDTO {
    private String lotNo;
    private String processName;
    private Double initialWortVolume;
    private Double currentWortVolume;
    private Double lossVolume;
    private LocalDateTime recordTime;
    private String productName;
    private String processStatus;
    private Double totalEfficiency; // 총 효율 (선택사항: 초기 대비 최종 워트량 비율)
}