package com.org.qualitycore.productionPlan.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ProductionPlanDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate planYm;      //계획년월
    private String productId;   // 제품코드
    private String productName; // 제품명
    private String sizeSpec;    // 규격
    private Integer planQty;    // 계획수량
    private String status;      // 상태
    private String beerType;     // 맥주종류
    private List<PlanLineDTO> allocatedLines;  // 생산 라인 정보
    private List<PlanMaterialDTO> materials;   // 자재 정보
    private String beerName;  //
    @JsonProperty("products")
    private List<ProductionPlanDTO> products;
    private MaterialRequestDTO materialRequests;
    private List<PlanMaterialDTO> rawMaterials;
    private List<PlanMaterialDTO> packagingMaterials;
    private String mainProductName;
    private Integer totalPlanQty;
    private String planId;

}

