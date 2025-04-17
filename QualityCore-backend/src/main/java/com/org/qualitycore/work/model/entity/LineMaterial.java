package com.org.qualitycore.work.model.entity;

import com.org.qualitycore.standardinformation.model.entity.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "workOrders")
@Entity
@Table(name = "LINE_MATERIAL")
@Builder
public class LineMaterial {

    @Id
    @Column(name = "LINE_MATERIAL_ID")
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    private String lineMaterialId;

    @Column(name = "MATERIAL_NAME")
    private String materialName;

    @Column(name = "TOTAL_QTY")
    private String totalQty;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "REQUIRED_QTY_PER_UNIT")
    private double requiredQtyPerUnit;

    @Column(name = "PROCESS_STEP")
    private String processStep;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOT_NO")
    private WorkOrders workOrders;



    // 남규 분쇄 공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "GRINDING_ID")  // 외래키 설정
    private MaterialGrinding materialGrinding;

    // 남규  당화 공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "MASHING_ID")  // 외래키 설정
    private MashingProcess mashingProcess;

    // 남규 여과 공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "FILTRATION_ID")  // 외래키 설정
    private FiltrationProcess filtrationProcess;

    // 남규 끓임 공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "BOILING_ID")  // 외래키 설정
    private BoilingProcess boilingProcess;

    // 남규 냉각 공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "COOLING_ID")  // 외래키 설정
    private CoolingProcess coolingProcess;

    // 남규 발효 상세 공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "FERMENTATION_ID")  // 외래키 설정
    private FermentationDetails fermentationDetails;

    // 남규 숙성 상세 공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "MATURATION_ID")  // 외래키 설정
    private MaturationDetails maturationDetails;

    // 남규 숙성 후 여과  공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "MFILTRATION_ID")  // 외래키 설정
    private PostMaturationFiltration postMaturationFiltration;

    // 남규 탄산 조정  공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "CARBONATION_ID")  // 외래키 설정
    private CarbonationProcess carbonationProcess;

    // 남규 패키징 및 출하  공정 추가함 fk 추가
    @ManyToOne
    @JoinColumn(name = "PACKAGING_ID")  // 외래키 설정
    private PackagingAndShipment packagingAndShipment;
}

