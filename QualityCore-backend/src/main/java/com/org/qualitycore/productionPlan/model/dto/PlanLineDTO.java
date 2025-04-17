package com.org.qualitycore.productionPlan.model.dto;

import com.org.qualitycore.productionPlan.model.entity.PlanLine;
import com.org.qualitycore.productionPlan.model.entity.PlanProduct;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PlanLineDTO {
    private String planLineId;
    private String planProductId;
    private Integer lineNo;
    private Integer planBatchNo;
    private Integer planQty;
    private Integer allocatedQty; // 추가
    private LocalDate startDate;
    private LocalDate endDate;
    private String productId;


    public static PlanLineDTO fromEntity(PlanLine planLine) {
        return new PlanLineDTO(
                planLine.getPlanLineId(),
                planLine.getPlanProduct().getPlanProductId(),
                planLine.getLineNo(),
                planLine.getPlanBatchNo(),
                planLine.getPlanQty(),
                null,
                planLine.getStartDate(),
                planLine.getEndDate(),
                planLine.getPlanProduct().getProductId()
        );
    }

    public PlanLine toEntity() {
        PlanProduct planProduct = new PlanProduct();
        planProduct.setPlanProductId(this.planProductId);
        planProduct.setProductId(this.productId);

        PlanLine planLine = new PlanLine();
        planLine.setPlanProduct(planProduct);
        planLine.setLineNo(this.lineNo);
        planLine.setPlanBatchNo(this.planBatchNo);
        planLine.setPlanQty(this.allocatedQty);
        planLine.setStartDate(this.startDate);
        planLine.setEndDate(this.endDate);
        planLine.setProductId(this.productId);

        return planLine;
    }
}
