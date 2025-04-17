package com.org.qualitycore.productionPlan.model.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.org.qualitycore.productionPlan.model.entity.PlanLine;
import com.org.qualitycore.productionPlan.model.entity.QPlanLine;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlanLineRepository {

    private final JPAQueryFactory queryFactory;
    private final SpringDataPlanLineRepository springDataPlanLineRepository;

//    // 특정 제품의 생산라인 배정 정보 조회 (QueryDSL)
//    public List<PlanLine> findProductionLinesByProductId(String planProductId) {
//        System.out.println("📌 [PlanLineRepository] Query 실행 - planProductId: " + planProductId);
//
//        List<PlanLine> result = queryFactory
//                .selectFrom(QPlanLine.planLine)
//                .where(QPlanLine.planLine.planProduct.planProductId.eq(planProductId))
//                .fetch();
//
//        System.out.println("📌 [PlanLineRepository] Query 결과 개수: " + result.size());
//
//        if (result.isEmpty()) {
//            System.out.println("⚠️ [PlanLineRepository] 해당 제품의 생산 라인 데이터 없음!");
//        }
//
//        return result;
//    }


    // JPA 기본 저장 메서드 활용
    public void saveAll(List<PlanLine> planLines) {
        springDataPlanLineRepository.saveAll(planLines);
    }

    public List<PlanLine> findProductionLinesByPlanProductId(String planProductId) {
        return queryFactory
                .selectFrom(QPlanLine.planLine)
                .where(QPlanLine.planLine.planProduct.planProductId.eq(planProductId)) // ✅ planProductId 사용
                .fetch();
    }

    public String findMaxPlanLineId() {
        return queryFactory
                .select(QPlanLine.planLine.planLineId.max())
                .from(QPlanLine.planLine)
                .fetchOne();
    }

    public PlanLine save(PlanLine planLine) {
        return springDataPlanLineRepository.save(planLine);
    }



}
