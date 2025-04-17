package com.org.qualitycore.productionPlan.model.repository;

import com.org.qualitycore.productionPlan.model.dto.ProductionPlanDTO;
import com.org.qualitycore.productionPlan.model.entity.QPlanMst;

import com.org.qualitycore.productionPlan.model.entity.QPlanProduct;

import com.org.qualitycore.productionPlan.model.entity.QProductionPlan;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PlanRepository {

    private final JPAQueryFactory queryFactory;
    public List<ProductionPlanDTO> findProductionPlans(LocalDate startDate, LocalDate endDate, String status) {
        QPlanMst planMst = QPlanMst.planMst;
        QProductionPlan productionPlan = QProductionPlan.productionPlan;

        BooleanBuilder whereClause = new BooleanBuilder();
        whereClause.and(planMst.planYm.between(startDate, endDate));

        if (status != null && !status.isBlank()) {
            whereClause.and(planMst.status.eq(status));
        }

        // 서브쿼리를 별도로 추출
        List<Tuple> results = queryFactory
                .select(
                        planMst.planId,
                        planMst.planYm,

                        // ✅ 대표 제품명 (최소 제품명)
                        productionPlan.productName.min(),

                        // ✅ Oracle에서 모든 `sizeSpec`을 하나의 문자열로 합쳐서 가져옴 (중복 제거)
                        Expressions.stringTemplate(
                                "LISTAGG(DISTINCT {0}, ', ') WITHIN GROUP (ORDER BY {0})", productionPlan.sizeSpec
                        ),

                        // ✅ 총 계획 수량
                        productionPlan.planQty.sum(),

                        planMst.status,

                        // ✅ 제품 개수
                        Expressions.stringTemplate(
                                "COUNT(DISTINCT {0})", productionPlan.productName
                        )
                )
                .from(planMst)
                .leftJoin(productionPlan).on(productionPlan.planId.eq(planMst.planId))
                .where(whereClause)
                .groupBy(planMst.planId, planMst.planYm, planMst.status)
                .fetch();

        // 결과 변환
        return results.stream()
                .map(tuple -> {
                    ProductionPlanDTO dto = new ProductionPlanDTO();
                    dto.setPlanId(tuple.get(0, String.class));
                    dto.setPlanYm(tuple.get(1, LocalDate.class));

                    // 대표 제품명 생성
                    String productName = tuple.get(2, String.class);
                    Long productCount = tuple.get(6, Long.class);
                    dto.setMainProductName(
                            productCount > 1
                                    ? productName + " 외 " + (productCount - 1) + "개"
                                    : productName
                    );

                    // ✅ `sizeSpec`을 하나의 문자열로 저장
                    dto.setSizeSpec(tuple.get(3, String.class));

                    dto.setTotalPlanQty(tuple.get(4, Integer.class));
                    dto.setStatus(tuple.get(5, String.class));

                    return dto;
                })
                .collect(Collectors.toList());
    }




}
