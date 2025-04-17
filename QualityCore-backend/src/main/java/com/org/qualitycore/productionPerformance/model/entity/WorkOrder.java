package com.org.qualitycore.productionPerformance.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "WorkOrder")
@Table(name = "WORK_ORDER")
@Data
@NoArgsConstructor
public class WorkOrder {
    @Id
    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "PLAN_LINE_ID")
    private String planLineId;

    @Column(name = "PLAN_PRODUCT_ID")
    private String planProductId;

    @Column(name = "PLAN_ID")
    private String planId;

    @Column(name = "EMP_ID")
    private String empId;

    @Column(name = "WORK_ETC")
    private String workEtc;
}