package com.org.qualitycore.productionPlan.model.dto;

import com.org.qualitycore.productionPlan.model.entity.ProductBom;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
public class ProductBomDTO {
    private String productId;
    private String productName;
    private String beerType;
    private String sizeSpec;
    private Double roomTemperature;
    private Integer stdProcessTime;
    private Integer fermentTime;
    private Double alcPercent;

    public static ProductBomDTO fromEntity(ProductBom productBom) {
        return new ProductBomDTO(
                productBom.getProductId(),
                productBom.getProductName(),
                productBom.getBeerType(),
                productBom.getSizeSpec(),
                productBom.getRoomTemperature(),
                productBom.getStdProcessTime(),
                productBom.getFermentTime(),
                productBom.getAlcPercent()
        );
    }
}
