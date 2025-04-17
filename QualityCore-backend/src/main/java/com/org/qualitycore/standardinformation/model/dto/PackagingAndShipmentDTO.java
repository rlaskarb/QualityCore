package com.org.qualitycore.standardinformation.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "패키징 및 출하 공정 DTO")
public class PackagingAndShipmentDTO {

    @Schema(description = "패키징 및 출하 공정 고유 ID", example = "PA001")
    private String packagingId;

    @Schema(description = "작업지시 ID", example = "LOT2025030801")
    private String lotNo;

    @Schema(description = "세척 및 살균 상태", example = "양호")
    private String cleaningAndSanitation;

    @Schema(description = "라벨링 및 코딩 상태", example = "양호")
    private String labelingAndCoding;

    @Schema(description = "충전 상태", example = "양호")
    private String fillingStatus;

    @Schema(description = "밀봉 상태", example = "양호")
    private String sealingStatus;

    @Schema(description = "포장 상태", example = "양호")
    private String packagingStatus;

    @Schema(description = "출하 날짜", example = "2025-03-10")
    private LocalDate shipmentDate;

    @Schema(description = "제품명", example = "아이유 맥주")
    private String productName;

    @Schema(description = "출하 수량", example = "500")
    private Double shipmentQuantity;

    @Schema(description = "목적지", example = "서울 물류센터")
    private String destination;

    @Schema(description = "메모 사항", example = "포장 검수 필요")
    private String notes;

    @Schema(description = "공정 추적 DTO" )
    private ProcessTrackingDTONam processTracking;
}
