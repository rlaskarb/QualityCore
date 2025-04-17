package com.org.qualitycore.work.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString(exclude = "lineMaterial")
@Table(name = "WORK_ORDER")
@Entity
@Schema(description = "작업지시서 관련 Entity")
public class WorkOrders {

    @Id
    @Column(name = "LOT_NO")
    private String lotNo; // 작업지시 번호(PK)

    @Column(name = "WORK_ETC")
    private String workEtc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_LINE_ID")
    private PlanLine planLine; // 생산라인 엔티티

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_PRODUCT_ID")
    private PlanProduct planProduct; // 생산제품 엔티티

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EMP_ID")
    private Employee employee; // 사원 엔티티

    @OneToMany(mappedBy = "workOrders", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<LineMaterial> lineMaterial;

    @OneToOne(fetch = FetchType.LAZY, mappedBy = "workOrders", cascade = CascadeType.ALL)
    private processTracking processTracking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PLAN_ID")
    private PlanMst planMst;

}

