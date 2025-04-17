package com.org.qualitycore.work.model.repository;

import com.org.qualitycore.work.model.entity.PlanMst;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkPlanMstRepository extends JpaRepository<PlanMst, String> {
}
