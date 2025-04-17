package com.org.qualitycore.productionPlan.model.repository;

import com.org.qualitycore.productionPlan.model.entity.PlanMaterial;
import com.org.qualitycore.productionPlan.model.entity.QPlanMaterial;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import com.org.qualitycore.productionPlan.model.entity.PlanProduct;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PlanMaterialRepository{
    private final JPAQueryFactory queryFactory;
    private final SpringDataPlanMaterialRepository springDataPlanMaterialRepository;


    // 최대 PlanMaterialId 찾는 메서드 추가
    public String findMaxPlanMaterialId() {
        String maxId = queryFactory
                .select(QPlanMaterial.planMaterial.planMaterialId.max())
                .from(QPlanMaterial.planMaterial)
                .fetchOne();

        return maxId;
    }

    public void saveAll(List<PlanMaterial> planMaterials) {
        springDataPlanMaterialRepository.saveAll(planMaterials);
    }


    public Optional<PlanMaterial> findById(String planMaterialId) {
        if (planMaterialId == null || planMaterialId.isEmpty()) {
            throw new IllegalArgumentException("🚨 planMaterialId는 null일 수 없습니다.");
        }
        return springDataPlanMaterialRepository.findById(planMaterialId);
    }

    public Optional<PlanMaterial> findByMaterialIdAndPlanProduct_PlanProductId(String materialId, String planProductId) {
        if (materialId == null || planProductId == null) {
            return Optional.empty(); // null인 경우 빈 Optional 반환
        }

        return Optional.ofNullable(
                queryFactory
                        .selectFrom(QPlanMaterial.planMaterial)
                        .where(
                                QPlanMaterial.planMaterial.materialId.eq(materialId)
                                        .and(QPlanMaterial.planMaterial.planProduct.planProductId.eq(planProductId))
                        )
                        .fetchFirst()
        );
    }

    public PlanMaterial save(PlanMaterial planMaterial) {
        return springDataPlanMaterialRepository.save(planMaterial);
    }


    // PlanMaterialRepository.java에 추가할 메서드
    public List<PlanMaterial> findByPlanProduct_PlanProductId(String planProductId) {
        if (planProductId == null || planProductId.isEmpty()) {
            throw new IllegalArgumentException("planProductId는 null일 수 없습니다.");
        }

        return queryFactory
                .selectFrom(QPlanMaterial.planMaterial)
                .where(QPlanMaterial.planMaterial.planProduct.planProductId.eq(planProductId))
                .fetch();
    }
}
