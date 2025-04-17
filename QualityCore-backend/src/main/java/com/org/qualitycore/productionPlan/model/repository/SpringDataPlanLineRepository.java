package com.org.qualitycore.productionPlan.model.repository;

import com.org.qualitycore.productionPlan.model.entity.PlanLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataPlanLineRepository extends JpaRepository<PlanLine, String> {
}
