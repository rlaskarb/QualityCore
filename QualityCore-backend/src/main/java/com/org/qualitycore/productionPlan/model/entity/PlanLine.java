package com.org.qualitycore.productionPlan.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity(name = "ProductionPlanLine")
@Table(name = "PLAN_LINE")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class PlanLine {

    @Id
    @Column(name = "PLAN_LINE_ID")
    private String planLineId;

    @ManyToOne
    @JoinColumn(name = "PLAN_PRODUCT_ID", nullable = false)
    private PlanProduct planProduct;

    @Column(name = "LINE_NO", nullable = false)
    private Integer lineNo;

    @Column(name = "PLAN_BATCH_NO", nullable = false)
    private Integer planBatchNo;

    @Column(name = "PLAN_QTY", nullable = false)
    private Integer planQty;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @Column(name = "PRODUCT_ID")
    private String productId;
}
