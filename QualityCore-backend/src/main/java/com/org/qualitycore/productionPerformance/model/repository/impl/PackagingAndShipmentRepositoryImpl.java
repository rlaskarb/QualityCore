package com.org.qualitycore.productionPerformance.model.repository.impl;


import com.org.qualitycore.productionPerformance.model.dto.ProductionPerformanceDTO;
import com.org.qualitycore.routing.model.entity.QPackagingAndShipment;
import com.org.qualitycore.productionPlan.model.entity.QPlanLine;
import com.org.qualitycore.productionPlan.model.entity.QPlanProduct;
import com.org.qualitycore.productionPerformance.model.entity.QWorkOrder;
import com.org.qualitycore.productionPerformance.model.repository.PackagingAndShipmentCustomRepository;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.temporal.ChronoUnit;

@RequiredArgsConstructor
public class PackagingAndShipmentRepositoryImpl implements PackagingAndShipmentCustomRepository {

    private final JPAQueryFactory queryFactory;
    private final EntityManager entityManager;

    @Override
    public List<ProductionPerformanceDTO> findMonthlyPerformance(YearMonth yearMonth, String productName) {
        System.out.println("Repository: findMonthlyPerformance 호출 - " + yearMonth + ", " + productName);
        QPackagingAndShipment ps = QPackagingAndShipment.packagingAndShipment;
        QWorkOrder wo = QWorkOrder.workOrder;
        QPlanProduct pp = QPlanProduct.planProduct;

        try {
            LocalDate startDate = yearMonth.atDay(1);
            LocalDate endDate = yearMonth.atEndOfMonth();
            System.out.println("조회 기간: " + startDate + " ~ " + endDate);

            List<Tuple> results = queryFactory
                    .select(
                            pp.productName,
                            ps.shipmentQuantity.sum(),
                            ps.goodQuantity.sum(), // 양품 수량 합계
                            Expressions.numberTemplate(Double.class,
                                    "ROUND(SUM({0}) / NULLIF(SUM({1}), 0) * 100, 2)",
                                    ps.goodQuantity, ps.shipmentQuantity) // 품질률 계산 수정
                    )
                    .from(ps)
                    .join(wo).on(ps.lotNo.eq(wo.lotNo))
                    .join(pp).on(wo.planProductId.eq(pp.planProductId))
                    .where(
                            ps.shipmentDate.between(startDate, endDate),
                            productNameEquals(productName, pp)
                    )
                    .groupBy(pp.productName)
                    .fetch();

            System.out.println("쿼리 실행 완료, 결과 수: " + results.size());

            // 안전한 타입 변환을 위한 로직
            List<ProductionPerformanceDTO> dtoList = new ArrayList<>();
            for (Tuple tuple : results) {
                ProductionPerformanceDTO dto = new ProductionPerformanceDTO();
                dto.setYearMonth(yearMonth.toString());
                dto.setProductName(tuple.get(0, String.class));

                // Number로 받아서 안전하게 변환
                Number totalQuantity = tuple.get(1, Number.class);
                Number goodQuantity = tuple.get(2, Number.class);
                Number qualityRate = tuple.get(3, Number.class);

                dto.setTotalQuantity(totalQuantity != null ? totalQuantity.intValue() : 0);
                dto.setGoodQuantity(goodQuantity != null ? goodQuantity.intValue() : 0);
                dto.setQualityRate(qualityRate != null ? qualityRate.doubleValue() : 0.0);

                dtoList.add(dto);
            }

            return dtoList;

        } catch (Exception e) {
            System.out.println("======= Repository 오류 발생 =======");
            System.out.println("오류 유형: " + e.getClass().getName());
            System.out.println("오류 메시지: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Map<String, Object>> findPlanVsActual(YearMonth yearMonth, String productName) {
        QPackagingAndShipment ps = QPackagingAndShipment.packagingAndShipment;
        QWorkOrder wo = QWorkOrder.workOrder;
        QPlanProduct pp = QPlanProduct.planProduct;

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<Tuple> results = queryFactory
                .select(
                        pp.productName,
                        pp.planQty,
                        ps.shipmentQuantity.sum(),
                        Expressions.numberTemplate(Double.class,
                                "ROUND(SUM({0}) / NULLIF({1}, 0) * 100, 2)",
                                ps.shipmentQuantity, pp.planQty)
                )
                .from(ps)
                .join(wo).on(ps.lotNo.eq(wo.lotNo))
                .join(pp).on(wo.planProductId.eq(pp.planProductId))
                .where(
                        ps.shipmentDate.between(startDate, endDate),
                        productNameEquals(productName, pp)
                )
                .groupBy(pp.productName, pp.planQty)
                .fetch();

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Tuple tuple : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("YEAR_MONTH", yearMonth.toString());
            map.put("PRODUCT_NAME", tuple.get(0, String.class));
            map.put("PLANNED_QUANTITY", tuple.get(1, Integer.class));
            map.put("ACTUAL_QUANTITY", tuple.get(2, Integer.class));
            map.put("ACHIEVEMENT_RATE", tuple.get(3, Double.class));
            resultList.add(map);
        }

        return resultList;
    }

    @Override
    public List<Map<String, Object>> findProductEfficiency() {
        QPackagingAndShipment ps = QPackagingAndShipment.packagingAndShipment;
        QWorkOrder wo = QWorkOrder.workOrder;
        QPlanProduct pp = QPlanProduct.planProduct;

        List<Tuple> results = queryFactory
                .select(
                        pp.productName,
                        ps.shipmentQuantity.sum().as("totalQuantity"),
                        ps.goodQuantity.sum().as("goodQuantity"),
                        Expressions.numberTemplate(Double.class,
                                "ROUND(SUM({0}) / NULLIF(SUM({1}), 0) * 100, 2)",
                                ps.goodQuantity, ps.shipmentQuantity).as("qualityRate")
                )
                .from(ps)
                .join(wo).on(ps.lotNo.eq(wo.lotNo))
                .join(pp).on(wo.planProductId.eq(pp.planProductId))
                .groupBy(pp.productName)
                .fetch();

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Tuple tuple : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("PRODUCT_NAME", tuple.get(0, String.class));
            map.put("TOTAL_QUANTITY", tuple.get(1, Number.class));
            map.put("GOOD_QUANTITY", tuple.get(2, Number.class));
            map.put("QUALITY_RATE", tuple.get(3, Number.class));

            // 디버깅용 로그 추가
            System.out.println("효율성 데이터: " + map);

            resultList.add(map);
        }

        return resultList;
    }

    private BooleanExpression productNameEquals(String productName, QPlanProduct pp) {
        return (productName != null && !productName.isEmpty()) ? pp.productName.eq(productName) : null;
    }
}