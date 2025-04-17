package com.org.qualitycore.productionPlan.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessStepDTO {

    private String planLineId;    // 계획 라인 ID
    private String productId;     // 제품 ID
    private Integer lineNo;       // 라인 번호
    private Integer batchNo;      // 배치 번호
    private String processName;   // 공정 이름 (분쇄, 당화, 여과 등)
    private LocalDateTime startTime;  // 공정 시작 시간
    private LocalDateTime endTime;    // 공정 종료 시간
}