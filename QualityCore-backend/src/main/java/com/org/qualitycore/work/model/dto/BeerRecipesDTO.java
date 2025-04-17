package com.org.qualitycore.work.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class BeerRecipesDTO {

    @Schema(description = "레시피 PK")
    private String id;

    @Schema(description = "맥주명")
    private String beerName;

    @Schema(description = "자재 PK")
    private String materialId;

    @Schema(description = "500ML 기준사용량")
    private String quantity;

    @Schema(description = "공정명")
    private String processStep;

    @Schema(description = "재료명")
    private String materialName;

    @Schema(description = "재료타입")
    private String materialType;

    @Schema(description = "단위")
    private String unit;

}
