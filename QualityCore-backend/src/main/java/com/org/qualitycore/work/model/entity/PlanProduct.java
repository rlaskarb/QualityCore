package com.org.qualitycore.work.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@Entity
@Table(name = "PLAN_PRODUCT")
public class PlanProduct {

    @Id
    @Column(name = "PLAN_PRODUCT_ID")
    private String planProductId;

    @Column(name = "PLAN_ID")
    private String planId;

    @Column(name = "PRODUCT_ID")
    private String productId;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "PLAN_QTY")
    private Integer planQty;

    @Column(name = "BASE_TEMP")
    private Integer baseTemp;

    @Column(name = "STD_PROCESS_TIME")
    private Integer stdProcessTime;

    @Column(name = "FERMENT_TIME")
    private Integer fermentTime;

    @Column(name = "ALC_PERCENT")
    private Integer alcPercent;

    @Column(name = "FILTER_TEMP")
    private Integer filterTemp;

    @Column(name = "SIZE_SPEC")
    private String sizeSpec;
}
