package com.org.qualitycore.productionPerformance.model.repository;

import com.org.qualitycore.productionPerformance.model.dto.ProductionPerformanceDTO;
import org.springframework.stereotype.Repository;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Repository
public interface PackagingAndShipmentCustomRepository {
    List<ProductionPerformanceDTO> findMonthlyPerformance(YearMonth yearMonth, String productName);
    List<Map<String, Object>> findPlanVsActual(YearMonth yearMonth, String productName);
    List<Map<String, Object>> findProductEfficiency();
}
