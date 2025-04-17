package com.org.qualitycore.productionPlan.model.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "ProductionPlanMaterial")
@Table(name = "PLAN_MATERIAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanMaterial {

    @Id
    @Column(name = "PLAN_MATERIAL_ID")
    private String planMaterialId;

    @ManyToOne
    @JoinColumn(name = "PLAN_PRODUCT_ID")
    private PlanProduct planProduct;

    @Column(name = "MATERIAL_ID")
    private String materialId;

    @Column(name = "MATERIAL_NAME")
    private String materialName;

    @Column(name = "MATERIAL_TYPE")
    private String materialType;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "STD_QTY")
    private Double stdQty;

    @Column(name = "PLAN_QTY")
    private Double planQty;

    @Column(name = "CURRENT_STOCK")
    private Double currentStock;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "BEER_NAME")
    private String beerName;



}
