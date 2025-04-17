package com.org.qualitycore.productionPlan.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.org.qualitycore.productionPlan.model.entity.PlanMaterial;
import com.org.qualitycore.productionPlan.model.entity.PlanProduct;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanMaterialDTO {


    private String planMaterialId;
    private String planProductId;
    private String materialId;
    private String materialName;
    private String materialType;
    private String unit;
    private Double stdQty = 0.0;
    private Double planQty = 0.0;
    private Double currentStock = 0.0;
    private String status;
    private Double shortageQty = 0.0;
    private String beerName;
    private String productId;

    public static PlanMaterialDTO fromEntity(PlanMaterial entity) {
        PlanMaterialDTO dto = new PlanMaterialDTO();
        dto.setPlanMaterialId(entity.getPlanMaterialId());
        dto.setPlanProductId(entity.getPlanProduct() != null ? entity.getPlanProduct().getPlanProductId() : null);
        dto.setMaterialId(entity.getMaterialId());
        dto.setMaterialName(entity.getMaterialName());
        dto.setMaterialType(entity.getMaterialType());
        dto.setUnit(entity.getUnit());
        dto.setStdQty(entity.getStdQty());
        dto.setPlanQty(entity.getPlanQty());
        dto.setCurrentStock(entity.getCurrentStock());
        dto.setStatus(entity.getStatus());
        dto.setBeerName(entity.getBeerName());

        // shortageQty 계산
        if ("부족".equals(entity.getStatus()) && entity.getPlanQty() != null && entity.getCurrentStock() != null) {
            dto.setShortageQty(entity.getPlanQty() - entity.getCurrentStock());
        } else {
            dto.setShortageQty(0.0);
        }

        return dto;
    }

    public PlanMaterial toEntity() {
        PlanMaterial entity = new PlanMaterial();
        entity.setPlanMaterialId(this.planMaterialId);

        if (this.planProductId != null) {
            PlanProduct planProduct = new PlanProduct();
            planProduct.setPlanProductId(this.planProductId);
            entity.setPlanProduct(planProduct);
        }

        entity.setMaterialId(this.materialId);
        entity.setMaterialName(this.materialName);
        entity.setMaterialType(this.materialType);
        entity.setUnit(this.unit);
        entity.setStdQty(this.stdQty);
        entity.setPlanQty(this.planQty);
        entity.setCurrentStock(this.currentStock);
        entity.setStatus(this.status);
        entity.setBeerName(this.beerName);

        return entity;
    }
}