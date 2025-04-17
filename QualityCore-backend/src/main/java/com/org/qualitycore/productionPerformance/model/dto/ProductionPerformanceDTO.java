package com.org.qualitycore.productionPerformance.model.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProductionPerformanceDTO {
    private String yearMonth;
    private String productName;
    private Integer totalQuantity;
    private Integer goodQuantity;
    private Double qualityRate;
    private Integer planQuantity;
    private Double achievementRate;

    // 일별 생산 데이터를 위한 추가 필드
    private LocalDate productionDate;
    private String lotNo;
    private String status;

    // 생산 효율성 데이터를 위한 추가 필드
    private Double avgProductionTimeMinutes;
    private Integer avgBatchSize;

    // 편의 메서드: 생산일별 그룹화를 위한 키 생성
    public String getDateKey() {
        return productionDate != null ? productionDate.toString() : "";
    }

    // 편의 메서드: 제품별 그룹화를 위한 키 생성
    public String getProductKey() {
        return productName != null ? productName : "";
    }
}
