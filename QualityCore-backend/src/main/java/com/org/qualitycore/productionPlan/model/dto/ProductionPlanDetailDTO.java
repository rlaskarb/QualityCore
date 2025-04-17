package com.org.qualitycore.productionPlan.model.dto;

import com.org.qualitycore.productionPlan.model.entity.PlanLine;
import com.org.qualitycore.productionPlan.model.entity.PlanMst;
import com.org.qualitycore.productionPlan.model.entity.PlanProduct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPlanDetailDTO {

    // 생산 계획 기본 정보
    private PlanMst planMst;

    // 제품 정보 목록
    private List<PlanProduct> planProducts;

    // 라인 정보 목록
    private List<PlanLine> planLines;

    // 공정 단계 정보 목록
    private List<ProcessStepDTO> processSteps;

    // 제품별 맥주 타입 매핑
    private Map<String, String> productBeerTypes;

    // 자재 정보 (원자재, 부자재)
    private List<PlanMaterialDTO> rawMaterials;
    private List<PlanMaterialDTO> packagingMaterials;

    // 자재 요청 정보
    private MaterialRequestDTO materialRequests;
}