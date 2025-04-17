package com.org.qualitycore.productionPlan.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "MaterialWarehouse")
@Table(name = "MATERIAL_WAREHOUSE")
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor

public class MaterialWarehouse {

    @Id
    @Column(name = "MATERIAL_ID")
    private String materialId;

    @Column(name = "MATERIAL_NAME")
    private String materialName;

    @Column(name = "MATERIAL_TYPE")
    private String materialType;

    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "CURRENT_STOCK")
    private Double currentStock;

    @Column(name = "UNIT")
    private String unit;

}
