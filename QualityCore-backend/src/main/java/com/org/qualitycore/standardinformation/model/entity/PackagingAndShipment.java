package com.org.qualitycore.standardinformation.model.entity;

import com.org.qualitycore.work.model.entity.LineMaterial;
import com.org.qualitycore.work.model.entity.processTracking;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PACKAGING_AND_SHIPMENT")
@Schema(description = "패키징 및 출하 공정 엔티티")
public class PackagingAndShipment {

    @Id
    @Column(name = "PACKAGING_ID", nullable = false, updatable = false)
    @Schema(description = "패키징 및 출하 공정 고유 ID", example = "PA001")
    private String packagingId;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "packagingAndShipment")
    @Schema(description = "작업지시 ID" , example ="LOT2025021301")
    private List<LineMaterial> lineMaterials;

    @ManyToOne(fetch = FetchType.LAZY )
    @Schema(description = "공정상태 체크" )
    private processTracking processTracking;

    @Column(name = "LOT_NO", nullable = false)
    @Schema(description = "작업지시 ID", example = "LOT2025030801")
    private String lotNo;

    @Column(name = "CLEANING_AND_SANITATION", nullable = false)
    @Schema(description = "세척 및 살균 상태", example = "양호")
    private String cleaningAndSanitation;

    @Column(name = "LABELING_AND_CODING", nullable = false)
    @Schema(description = "라벨링 및 코딩 상태", example = "양호")
    private String labelingAndCoding;

    @Column(name = "FILLING_STATUS", nullable = false)
    @Schema(description = "충전 상태", example = "양호")
    private String fillingStatus;

    @Column(name = "SEALING_STATUS", nullable = false)
    @Schema(description = "밀봉 상태", example = "양호")
    private String sealingStatus;

    @Column(name = "PACKAGING_STATUS", nullable = false)
    @Schema(description = "포장 상태", example = "양호")
    private String packagingStatus;

    @CreationTimestamp
    @Column(name = "SHIPMENT_DATE", nullable = false)
    @Schema(description = "출하 날짜", example = "2025-03-10")
    private LocalDate shipmentDate;

    @Column(name = "PRODUCT_NAME", nullable = false)
    @Schema(description = "제품명", example = "아이유 맥주")
    private String productName;

    @Column(name = "SHIPMENT_QUANTITY", nullable = false)
    @Schema(description = "출하 수량", example = "500")
    private Double shipmentQuantity;

    @Column(name = "DESTINATION", nullable = false)
    @Schema(description = "목적지", example = "서울 물류센터")
    private String destination;

    @Column(name = "NOTES")
    @Schema(description = "메모 사항", example = "포장 검수 필요")
    private String notes;

    @Override
    public String toString() {
        return "PackagingAndShipment{" +
                "packagingId='" + packagingId + '\'' +
                ", lotNo='" + lotNo + '\'' +
                ", cleaningAndSanitation='" + cleaningAndSanitation + '\'' +
                ", labelingAndCoding='" + labelingAndCoding + '\'' +
                ", fillingStatus='" + fillingStatus + '\'' +
                ", sealingStatus='" + sealingStatus + '\'' +
                ", packagingStatus='" + packagingStatus + '\'' +
                ", shipmentDate=" + shipmentDate +
                ", productName='" + productName + '\'' +
                ", shipmentQuantity=" + shipmentQuantity +
                ", destination='" + destination + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}