package com.org.qualitycore.work.model.repository;

import com.org.qualitycore.work.model.entity.PlanProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkPlanProductRepository extends JpaRepository<PlanProduct, String> { }
