package com.org.qualitycore.work.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@DynamicInsert
@Table(name = "PLAN_LINE")
public class PlanLine {

    @Id
    @Column(name = "PLAN_LINE_ID")
    private String planLineId; // 생산라인 배정 ID

    @Column(name = "PLAN_PRODUCT_ID")
    private String planProductId; // 생산계획 제품 ID

    @Column(name = "LINE_NO")
    private Integer lineNo; // 생산라인 번호

    @Column(name = "PRODUCT_ID")
    private String productId; // 제품 ID

    @Column(name = "PLAN_BATCH_NO")
    private Integer planBatchNo; // 생산 회차 번호

    @Column(name = "PLAN_QTY")
    private Integer planQty; // 해당 라인 배정수량

    @Column(name = "START_DATE")
    private Date startDate; // 생산시작일

    @Column(name = "END_DATE")
    private Date endDate; // 생산종료일

}
