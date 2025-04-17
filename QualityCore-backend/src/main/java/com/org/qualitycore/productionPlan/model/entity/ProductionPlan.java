package com.org.qualitycore.productionPlan.model.entity;


import jakarta.persistence.*;
import lombok.*;
import jakarta.persistence.Id;


@Entity
@Table(name = "PLAN_PRODUCT")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class ProductionPlan {


//     이곳은 생산계획조회 DTO 입니다.
    @Id
    @Column(name = "PLAN_PRODUCT_ID")
    private String planProductId;     // 생산계획제품 ID

    @Column(name = "PLAN_ID")
    private String planId;            // 생산계획 ID

    @Column(name = "PRODUCT_NAME")
    private String productName;       // 제품명

    @Column(name = "SIZE_SPEC")
    private String sizeSpec;           // 규격

    @Column(name = "PLAN_QTY")
    private Integer planQty;          // 계획수량

    @Column(name = "PRODUCT_ID")
    private String productId;  // 제품 ID

//    private String planProductId;
//    private String planId;
//    private String productName;
//    private String sizeSpec;
//    private Integer planQty;
//    private String productId;
}
