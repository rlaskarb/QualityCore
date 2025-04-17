package com.org.qualitycore.productionPerformance.model.repository;

import com.org.qualitycore.productionPerformance.model.entity.WorkOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkOrderRepository extends JpaRepository<WorkOrder, String> {
    @Query("SELECT w FROM WorkOrder w WHERE w.planId = :planId")
    List<WorkOrder> findByPlanId(@Param("planId") String planId);
}
