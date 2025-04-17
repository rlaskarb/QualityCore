package com.org.qualitycore.productionPlan.model.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PLAN_MST")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PlanMst {

    @Id
    @Column(name= "PLAN_ID")
    private String planId;

    @Column(name = "PLAN_YM", nullable = false)
    private LocalDate planYm;

    @Column(name="STATUS", nullable = false)
    private String status = "미확정";

    @Column(name = "CREATED_BY", nullable = false)
    private String createdBy; // 추가됨

    @OneToMany(mappedBy = "planMst", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanProduct> planProducts;

    // 💡 INSERT 전에 자동으로 기본값 설정
    @PrePersist
    public void prePersist() {
        if (this.createdBy == null) {
            this.createdBy = "SYSTEM";  // 기본값 설정
        }
    }

    // 연관관계 추가
    @OneToMany(mappedBy = "planMst")
    private List<PlanProduct> products = new ArrayList<>();

    // 데이터베이스에 저장되지 않는 필드
    @Transient
    private String mainProductName;

    @Transient
    private Integer totalPlanQty;

    // 첫 번째 제품의 이름 가져오기
    public String getMainProductName() {
        if (products != null && !products.isEmpty()) {
            return products.get(0).getProductName();
        }
        return null;
    }

    // 모든 제품의 총 계획 수량 계산
    public Integer getTotalPlanQty() {
        if (products != null) {
            return products.stream()
                    .mapToInt(PlanProduct::getPlanQty)
                    .sum();
        }
        return 0;
    }

}
