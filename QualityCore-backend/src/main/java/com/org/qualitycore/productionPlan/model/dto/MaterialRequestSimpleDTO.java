package com.org.qualitycore.productionPlan.model.dto;

import com.org.qualitycore.productionPlan.model.entity.MaterialRequest;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequestSimpleDTO {
    private String requestId;
    private String planMaterialId;
    private String materialId;
    private String materialName;
    private Double requestQty;
    private LocalDate deliveryDate;
    private String reason;
    private String note;
    private LocalDate requestDate;

    public static MaterialRequestSimpleDTO fromEntity(MaterialRequest entity) {
        MaterialRequestSimpleDTO dto = new MaterialRequestSimpleDTO();
        dto.setRequestId(entity.getRequestId());
        dto.setRequestQty(entity.getRequestQty());
        dto.setDeliveryDate(entity.getDeliveryDate());
        dto.setReason(entity.getReason());
        dto.setNote(entity.getNote());
        dto.setRequestDate(entity.getRequestDate());

        // ✅ 기존 자재 요청이면 `PlanMaterial`에서 정보 가져오기
        if (entity.getPlanMaterial() != null) {
            dto.setMaterialId(entity.getPlanMaterial().getMaterialId());
            dto.setMaterialName(entity.getPlanMaterial().getMaterialName());
        }
        // ✅ 신규 자재 요청이면 직접 저장된 `materialId`, `materialName` 사용
        else {
            dto.setMaterialId(entity.getMaterialId());

        }

        return dto;
    }
}


