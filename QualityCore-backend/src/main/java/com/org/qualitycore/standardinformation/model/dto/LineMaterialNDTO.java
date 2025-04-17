package com.org.qualitycore.standardinformation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import static com.org.qualitycore.work.model.entity.QWorkOrders.workOrders;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "라인별 자재 소요 DTO")
public class LineMaterialNDTO {

    @Schema(description = "라인별 자재 소요 ID" , example = "LM001")
    private String lineMaterialId;

    @Schema(description = "자재명", example = "패일 몰트")
    private String materialName;

    @Schema(description = "총 소요량" ,example = "318")
    private String totalQty;

    @Schema(description = "자재단위", example = "kg")
    private String unit;

    @Schema(description = "1개 맥주 자재 소요량" , example = "0.1")
    private double requiredQtyPerUnit;

    @Schema(description = "작업지시 ID" , example = "LOT2025021201")
    private String lotNo;  // workOrders 대신 lotNo만 포함


}


