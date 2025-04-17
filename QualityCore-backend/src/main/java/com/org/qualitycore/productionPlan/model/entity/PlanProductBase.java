package com.org.qualitycore.productionPlan.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.Id;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public abstract class PlanProductBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PLAN_PRODUCT_ID")
    private String planProductId; // 생산계획 제품 ID

    @Column(name = "PLAN_ID", nullable = false)
    private String planId;    // 생산계획 ID

    @Column(name = "PRODUCT_ID", nullable = false)
    private String productId;   // 제품 코드

    @Column(name = "PRODUCT_NAME", nullable = false)
    private String productName;   // 제품명

    @Column(name = "PLAN_QTY", nullable = false)
    private Integer planQty;      // 계획 수량
}
