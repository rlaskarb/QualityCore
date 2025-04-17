package com.org.qualitycore.work.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LineMaterialDTO {

    @Schema(description = "pk")
    private String lineMaterialId;

    @Schema(description = "자재명")
    private String materialName;

    @Schema(description =   "자재 총 수량")
    private Integer totalQty;

    @Schema(description = "자재 단위")
    private String unit;

    @Schema(description = "1개 맥주에 필요한 자재 소요량")
    private double requiredQtyPerUnit;

    @Schema(description = "공정")
    private String processStep;

}
