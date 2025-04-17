package com.org.qualitycore.productionPlan.model.dto;

import com.org.qualitycore.productionPlan.model.entity.MaterialRequest;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequestDTO {

    private List<MaterialRequestInfo> materials; // 부족 자재 목록
    private String requestId;
    private String planMaterialId;
    private Double requestQty;
    private LocalDate deliveryDate;
    private String reason;
    private String note;
    private String status;
    private String materialId;
    private String materialName;
    private LocalDate requestDate;

    // 내부 클래스 (기존 코드 유지)
    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MaterialRequestInfo {
        private String materialId;
        private String materialName;
        private Double requestQty;
        private String productId;
    }

    // ✅ Entity → DTO 변환 추가 (서비스에서 사용 가능)
    public static MaterialRequestDTO fromEntity(MaterialRequest request) {
        System.out.println("📌 [DEBUG] fromEntity() 호출됨 - 요청 ID: " + request.getRequestId());


        MaterialRequestDTO dto = new MaterialRequestDTO();
        dto.setRequestId(request.getRequestId());
        dto.setRequestQty(request.getRequestQty());
        dto.setDeliveryDate(request.getDeliveryDate());
        dto.setReason(request.getReason());
        dto.setNote(request.getNote());
        dto.setStatus(request.getStatus() != null ? request.getStatus() : "미발주");;
        dto.setMaterialId(request.getMaterialId());
        dto.setRequestDate(request.getRequestDate());

        // ✅ DTO 변환 중, materialName을 강제로 할당
        if (request.getPlanMaterial() != null && request.getPlanMaterial().getMaterialName() != null) {
            dto.setMaterialName(request.getPlanMaterial().getMaterialName());
            System.out.println("✅ DTO 변환: 자재명 - " + request.getPlanMaterial().getMaterialName());
        } else {
            dto.setMaterialName("미확인 자재");
            System.out.println("❌ DTO 변환: PlanMaterial이 NULL이므로 '미확인 자재' 설정");
        }
        return dto;
    }


    // ✅ DTO → Entity 변환 (`toEntity` 수정)
    public MaterialRequest toEntity() {
        MaterialRequest entity = new MaterialRequest();
        entity.setRequestId(this.requestId);
        entity.setRequestQty(this.requestQty);
        entity.setDeliveryDate(this.deliveryDate);
        entity.setReason(this.reason);
        entity.setNote(this.note);
        entity.setStatus(this.status);
        entity.setMaterialId(this.materialId);
        entity.setRequestDate(this.requestDate);
        return entity;
    }
}
