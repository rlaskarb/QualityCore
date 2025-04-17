package com.org.qualitycore.productionPlan.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class ProductBom {


    @Id
    private String productId;
    private String productName;
    private String beerType;
    private String sizeSpec;
    private Double roomTemperature;
    private Integer stdProcessTime;
    private Integer fermentTime;
    private Double alcPercent;

}
