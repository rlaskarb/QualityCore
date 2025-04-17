package com.org.qualitycore.productionPlan.model.repository;

import com.org.qualitycore.productionPlan.model.entity.PlanMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanMstRepository extends JpaRepository <PlanMst,String>{
    @Query("SELECT MAX(p.planId) FROM PlanMst p")
    String findMaxPlanId(); // 현재 가장 큰 PLAN_ID 조회
}
