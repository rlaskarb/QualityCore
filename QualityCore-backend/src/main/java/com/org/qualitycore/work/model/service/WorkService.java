package com.org.qualitycore.work.model.service;

import com.org.qualitycore.work.model.dto.*;
import com.org.qualitycore.work.model.entity.*;
import com.org.qualitycore.work.model.repository.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Schema(description = "작업지시서 관련 Service")
public class WorkService {

    private final WorkRepository workRepository;
    private final JPAQueryFactory queryFactory;
    private final WorkEmployeeRepository employeeRepository;
    private final WorkPlanLineRepository planLineRepository;
    private final WorkPlanProductRepository planProductRepository;
    private final ProcessTrackingRepository processTrackingRepository;
    private final WorkPlanMstRepository workPlanMstRepository;
    private final ModelMapper modelMapper;
    private final LotSequenceRepository lotSequenceRepository;

    // 작업지시서 전체 조회
    public Page<WorkFindAllDTO> findAllWorkOrders(Pageable pageable, String workTeam, String productName, String lotNo, String lineNo, Date startDate, Date endDate) {
        QWorkOrders wo = QWorkOrders.workOrders;
        QPlanLine pl = QPlanLine.planLine;
        QPlanProduct pp = QPlanProduct.planProduct;
        QLineMaterial lm = QLineMaterial.lineMaterial;
        QEmployee e = QEmployee.employee;
        QprocessTracking pt = QprocessTracking.processTracking;

        BooleanBuilder where = new BooleanBuilder();

        if (StringUtils.hasText(workTeam)) {
            where.and(e.workTeam.containsIgnoreCase(workTeam));
        }
        if (StringUtils.hasText(productName)) {
            where.and(pp.productName.containsIgnoreCase(productName));
        }
        if (StringUtils.hasText(lotNo)) {
            where.and(wo.lotNo.containsIgnoreCase(lotNo));
        }
        if (StringUtils.hasText(lineNo)) {
            where.and(pl.lineNo.eq(Integer.parseInt(lineNo)));
        }
        if (startDate != null) {
            where.and(pl.startDate.goe(startDate));
        }
        if (endDate != null) {
            where.and(pl.endDate.loe(endDate));
        }

        // 전체 데이터 개수 조회 (필터링 적용)
        long totalCount = Optional.ofNullable(queryFactory
                        .select(wo.count())
                        .from(wo)
                        .join(pl).on(wo.planLine.planLineId.eq(pl.planLineId))
                        .join(pp).on(pl.planProductId.eq(pp.planProductId))
                        .join(e).on(wo.employee.empId.eq(e.empId))
                        .join(pt).on(wo.lotNo.eq(pt.workOrders.lotNo))
                        .where(where)
                        .fetchOne())
                .orElse(0L);

        // 페이지네이션 적용하여 데이터 조회 (필터링 적용)
        List<WorkFindAllDTO> workOrders = queryFactory
                .select(Projections.fields(WorkFindAllDTO.class,
                        wo.lotNo.as("lotNo"),
                        wo.workEtc.as("workEtc"),
                        pp.planProductId.as("planProductId"),
                        pp.productName.as("productName"),
                        pt.trackingId.as("trackingId"),
                        pt.processStatus.as("processStatus"),
                        pt.processName.as("processName"),
                        pt.statusCode.as("statusCode"),
                        e.empId.as("empId"),
                        e.empName.as("empName"),
                        e.workTeam.as("workTeam"),
                        pl.planLineId.as("planLineId"),
                        pl.lineNo.as("lineNo"),
                        pl.startDate.as("startDate"),
                        pl.endDate.as("endDate"),
                        pl.planQty.as("planQty")
                ))
                .from(wo)
                .join(pl).on(wo.planLine.planLineId.eq(pl.planLineId))
                .join(pp).on(pl.planProductId.eq(pp.planProductId))
                .join(e).on(wo.employee.empId.eq(e.empId))
                .join(pt).on(wo.lotNo.eq(pt.workOrders.lotNo))
                .where(where)
                .orderBy(wo.lotNo.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 각 작업지시서에 자재 정보 추가
        for (WorkFindAllDTO workOrder : workOrders) {
            List<LineMaterialDTO> lineMaterials = queryFactory
                    .select(Projections.fields(LineMaterialDTO.class,
                            lm.lineMaterialId.as("lineMaterialId"),
                            lm.materialName.as("materialName"),
                            lm.totalQty.castToNum(Integer.class).as("totalQty"),
                            lm.unit.as("unit"),
                            lm.requiredQtyPerUnit.castToNum(Double.class).as("requiredQtyPerUnit"),
                            lm.processStep.as("processStep")
                    ))
                    .from(lm)
                    .where(lm.workOrders.lotNo.eq(workOrder.getLotNo()))
                    .fetch();

            workOrder.setLineMaterials(lineMaterials);
        }

        // Page 객체로 변환하여 반환
        return new PageImpl<>(workOrders, pageable, totalCount);
    }


    // 특정 작업지시서 조회
    public WorkFindAllDTO findByCodeWorkOrder(String lotNo) {
        QWorkOrders wo = QWorkOrders.workOrders;
        QPlanLine pl = QPlanLine.planLine;
        QPlanProduct pp = QPlanProduct.planProduct;
        QLineMaterial lm = QLineMaterial.lineMaterial;
        QEmployee e = QEmployee.employee;
        QprocessTracking pt = QprocessTracking.processTracking;

        WorkFindAllDTO workOrder = queryFactory
                .select(Projections.fields(WorkFindAllDTO.class,
                        wo.lotNo.as("lotNo"),
                        wo.workEtc.as("workEtc"),
                        pp.planProductId.as("planProductId"),
                        pp.productName.as("productName"),
                        pt.trackingId.as("trackingId"),
                        pt.processStatus.as("processStatus"),
                        pt.processName.as("processName"),
                        pt.statusCode.as("statusCode"),
                        e.empId.as("empId"),
                        e.empName.as("empName"),
                        e.workTeam.as("workTeam"),
                        pl.planLineId.as("planLineId"),
                        pl.lineNo.as("lineNo"),
                        pl.startDate.as("startDate"),
                        pl.endDate.as("endDate"),
                        pl.planQty.as("planQty")
                ))
                .from(wo)
                .join(pl).on(wo.planLine.planLineId.eq(pl.planLineId))
                .join(pp).on(pl.planProductId.eq(pp.planProductId))
                .join(e).on(wo.employee.empId.eq(e.empId))
                .join(pt).on(wo.lotNo.eq(pt.workOrders.lotNo))
                .where(wo.lotNo.eq(lotNo))
                .fetchOne();

        if (workOrder == null) {
            throw new IllegalArgumentException("작업지시서를 찾을 수 없습니다: " + lotNo);
        }

        List<LineMaterialDTO> lineMaterials = queryFactory
                .select(Projections.fields(LineMaterialDTO.class,
                        lm.lineMaterialId.as("lineMaterialId"),
                        lm.materialName.as("materialName"),
                        lm.totalQty.castToNum(Integer.class).as("totalQty"),
                        lm.unit.as("unit"),
                        lm.requiredQtyPerUnit.castToNum(Double.class).as("requiredQtyPerUnit"),
                        lm.processStep.as("processStep")
                ))
                .from(lm)
                .where(lm.workOrders.lotNo.eq(lotNo))
                .fetch();

        workOrder.setLineMaterials(lineMaterials);

        return workOrder;
    }

    // 작업지시서 등록
    @Transactional
    public void createWorkOrder(WorkFindAllDTO work) {
        // 사원 정보 조회
        Employee employee = employeeRepository.findById(work.getEmpId())
                .orElseThrow(() -> new IllegalArgumentException("사원을 찾을 수 없습니다: " + work.getEmpId()));

        // 생산라인 정보 조회
        PlanLine planLine = planLineRepository.findById(work.getPlanLineId())
                .orElseThrow(() -> new IllegalArgumentException("생산라인을 찾을 수 없습니다. planLineId: " + work.getPlanLineId()));

        // 제품, 진행 상태 조회
        PlanProduct planProduct = planProductRepository.findById(work.getPlanProductId())
                .orElseThrow(() -> new IllegalArgumentException("생산제품을 찾을 수 없습니다: " + work.getPlanProductId()));

        PlanMst planMst = workPlanMstRepository.findById(work.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("생산계획을 찾을 수 없습니다: " + work.getPlanId()));

        planMst.setStatus("생산지시");

        workPlanMstRepository.save(planMst);

        // 작업지시서 객체 생성 및 설정
        WorkOrders workOrder = modelMapper.map(work, WorkOrders.class);
        workOrder.setPlanMst(planMst);
        workOrder.setPlanProduct(planProduct);
        workOrder.setPlanLine(planLine);
        workOrder.setEmployee(employee);

        // 가장 최신 작업지시서 LOT 번호 조회 및 새 LOT 번호 생성
        String newWorkOrderId = generateNewWorkOrderId();
        workOrder.setLotNo(newWorkOrderId);  // 새 LOT 번호 설정

        List<LineMaterial> lineMaterials = work.getLineMaterials().stream()
                .map(lineMaterialDTO -> {
                    LineMaterial lineMaterial = modelMapper.map(lineMaterialDTO, LineMaterial.class);
                    lineMaterial.setWorkOrders(workOrder); // 자재 목록에 연결
                    return lineMaterial;
                })
                .collect(Collectors.toList());

        workOrder.setLineMaterial(lineMaterials);

        // 작업지시서 저장 (첫 번째 저장)
        workRepository.save(workOrder);

        // 진행상태 (ProcessTracking) 추가
        processTracking processTracking = new processTracking();
        processTracking.setWorkOrders(workOrder);  // 작업지시서와 연결

        // ProcessTracking 저장
        processTrackingRepository.save(processTracking);

        // 진행상태가 설정된 후 작업지시서를 업데이트 (두 번째 저장은 불필요)
        workOrder.setProcessTracking(processTracking);  // 작업지시서에 진행상태 연결
    }


    // auto increment 방식으로 작업지시서 번호 생성
    private String generateNewWorkOrderId() {
        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(DateTimeFormatter.BASIC_ISO_DATE); // yyyyMMdd

        // 시퀀스 조회/생성 (락으로 동시 접근 차단)
        LotSequence sequence = lotSequenceRepository.findBySequenceDate(currentDate)
                .orElseGet(() -> lotSequenceRepository.save(new LotSequence(currentDate)));

        int newSequence = sequence.getCurrentSequence();
        sequence.incrementSequence();
        lotSequenceRepository.save(sequence);

        return "LOT" + formattedDate + String.format("%02d", newSequence);
    }

    // 작업지시서 삭제
    @Transactional
    public void workOrderDelete(String lotNo) {

        workRepository.deleteById(lotNo);

        modelMapper.map(lotNo, WorkFindAllDTO.class);
    }

    // 직원전체조회
    public List<EmployeeDTO> employee() {

        List<Employee> employees = employeeRepository.findAll();

        return employees.stream().map(employee -> modelMapper.map(employee, EmployeeDTO.class)).collect(Collectors.toList());
    }

    // 맥주레시피
    public Map<String, Map<String, List<BeerRecipesDTO>>> beerRecipes() {
        QBeerRecipesWork br = QBeerRecipesWork.beerRecipesWork;
        QMaterialWarehouseWork mw = QMaterialWarehouseWork.materialWarehouseWork;

        List<BeerRecipesDTO> recipes = queryFactory
                .select(Projections.fields(BeerRecipesDTO.class,
                        br.id.as("id"),
                        br.beerName.as("beerName"),
                        br.quantity.as("quantity"),
                        br.processStep.as("processStep"),
                        br.material.materialId.as("materialId"),
                        mw.materialName.as("materialName"),
                        mw.materialType.as("materialType"),
                        mw.unit.as("unit")
                ))
                .from(br)
                .leftJoin(mw).on(br.material.materialId.eq(mw.materialId))
                .fetch();

        // materialId 기준 중복 제거 후 그룹화
        return recipes.stream()
                .collect(Collectors.groupingBy(
                        BeerRecipesDTO::getBeerName,
                        Collectors.groupingBy(
                                BeerRecipesDTO::getProcessStep,
                                Collectors.collectingAndThen(
                                        Collectors.toMap(
                                                BeerRecipesDTO::getMaterialId, // materialId 기준 중복 제거
                                                Function.identity(),
                                                (existing, replacement) -> existing,
                                                LinkedHashMap::new
                                        ),
                                        map -> new ArrayList<>(map.values()) // Map → List 변환
                                )
                        )
                ));
    }


    // 생산계획정보
    public List<PlanInfoDTO> workOrderPlanInfo() {
        QPlanMst pm = QPlanMst.planMst;
        QPlanProduct pp = QPlanProduct.planProduct;
        QPlanLine pl = QPlanLine.planLine;

        return queryFactory
                .select(Projections.fields(PlanInfoDTO.class,
                        pm.planId.as("planId"),
                        pm.status.as("status"),
                        pp.planProductId.as("planProductId"),
                        pp.productName.as("productName"),
                        pl.planLineId.as("planLineId"),
                        pl.lineNo.as("lineNo"),
                        pl.planQty.as("planQty"),
                        pl.startDate.as("startDate"),
                        pl.endDate.as("endDate")
                ))
                .from(pm)
                .join(pp).on(pm.planId.eq(pp.planId))
                .join(pl).on(pp.planProductId.eq(pl.planProductId))
                .where(pm.status.eq("확정"))
                .fetch();
    }

    // 맥주랭킹 서비스
    public List<BeerRankingDTO> getTop3BeerRanking() {
            QWorkOrders wo = QWorkOrders.workOrders;
            QPlanLine pl = QPlanLine.planLine;
            QPlanProduct pp = QPlanProduct.planProduct;

            // 이번 달의 첫날과 마지막 날 계산
            LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
            LocalDate lastDayOfMonth = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());

            // LocalDate → java.util.Date 변환
            Date firstDay = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date lastDay = Date.from(lastDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());

            return queryFactory
                    .select(Projections.constructor(BeerRankingDTO.class,
                            pp.productName,
                            pl.planQty.sum().longValue(),
                            pl.lineNo,
                            pl.startDate.min(),  // ✅ MIN 적용
                            pl.endDate.max()     // ✅ MAX 적용
                    ))
                    .from(wo)
                    .join(pl).on(wo.planLine.planLineId.eq(pl.planLineId))
                    .join(pp).on(pl.planProductId.eq(pp.planProductId))
                    .where(
                            pl.startDate.isNotNull()
                                    .and(pl.endDate.isNotNull())
                                    .and(pl.startDate.goe(firstDay).or(pl.startDate.loe(lastDay)))
                    )
                    .groupBy(pp.productName, pl.lineNo) // ✅ startDate, endDate 제거 가능
                    .orderBy(pl.planQty.sum().desc())
                    .limit(3)
                    .fetch();
        }
}

