package com.org.qualitycore.productionPlan.model.repository;

import com.org.qualitycore.productionPlan.model.entity.PlanMaterial;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPlanMaterialRepository extends JpaRepository<PlanMaterial, String> {
}
