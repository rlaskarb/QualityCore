package com.org.qualitycore.productionPlan.model.repository;

import com.org.qualitycore.productionPlan.model.entity.PlanProduct;
import com.org.qualitycore.productionPlan.model.entity.ProductionPlan;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository

public interface PlanProductRepository extends JpaRepository<PlanProduct, String> {
    @Query("SELECT MAX(p.planProductId) FROM PlanProduct p")
    String findMaxPlanProductId(); // 현재 가장 큰 PLAN_PRODUCT_ID 조회

    List<PlanProduct> findByPlanMst_PlanId(String planId);
}
