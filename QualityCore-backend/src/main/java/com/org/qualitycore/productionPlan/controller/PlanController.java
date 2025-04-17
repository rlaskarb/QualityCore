package com.org.qualitycore.productionPlan.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.exception.ResourceNotFoundException;
import com.org.qualitycore.productionPlan.model.dto.*;
import com.org.qualitycore.productionPlan.model.entity.MaterialRequest;
import com.org.qualitycore.productionPlan.model.entity.MaterialWarehouse;
import com.org.qualitycore.productionPlan.model.entity.PlanProduct;
import com.org.qualitycore.productionPlan.model.service.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "ProductionPlan",description = "생산계획 API_Controller")
public class PlanController {

    /*
    * /api/v1/products get -> 제품 전체조회
    * /api/v1/products/1402 get -> 1번 제품 상세 조회
    * /api/v1/products/1 post -> 1번 제품 등록
    * /api/v1/products/1 put -> 1번 제품 수정
    * /api/v1/products/1 delete -> 1번 제품 수정
    * 메소드 오버로딩 => 메소드 시그니처에 따라서 다르게 동작하는 메소드를 생성할 수 있다.
    * rest-api readme.md 파일 정리본 참조해서 rest-api 작성규칙!!!
    * http-status-code : 200, 201, 204, 403, 404, 401, 400, 500
    * */


    private final PlanService planService;

    // ✅ 응답 헤더 기본 설정
    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));
        return headers;
    }

    // 생산계획조회
    @GetMapping("/plans")
    public List<ProductionPlanDTO> findProductionPlans(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") String planYm,
            @RequestParam(required = false, defaultValue = "") String status // 기본값 추가
    ) {
        // 요청 로그
        System.out.println("요청 받은 planYm: " + planYm);
        System.out.println("요청 받은 status: " + status);

        // '2025-02' → '2025-02-01' ~ '2025-02-28' 변환
        LocalDate startDate = LocalDate.parse(planYm + "-01", DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());

        // 서비스 호출
        List<ProductionPlanDTO> result = planService.getAllProductionPlans(startDate, endDate, status);

        // 결과
        System.out.println("응답 데이터 개수: " + result.size());
        return result;
    }

    // 생산계획 step1
    @PostMapping("/plans/step1")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> createProductionPlan(@RequestBody ProductionPlanDTO dto) {
        String planProductId = planService.saveProductionPlan(dto);
        return ResponseEntity.ok(planProductId);
    }

    //제품 선택시 BOM정보 불러오기
    @GetMapping("/productBom/{productId}")
    public ResponseEntity<ProductBomDTO> getProductStandard(@PathVariable String productId) {
        ProductBomDTO productBom = planService.getProductStandard(productId);
        return ResponseEntity.ok(productBom);
    }


    @GetMapping("/products")
    public ResponseEntity<List<ProductBomDTO>> getAllProducts(){
        List<ProductBomDTO> products = planService.getAllProducts();
        return ResponseEntity.ok(products);
    }

    // 특정 제품의 생산라인 배정 조회
    @GetMapping("/plans/lines/{planProductId}")
    public ResponseEntity<Message> getProductionLines(@PathVariable String planProductId) {
        List<PlanLineDTO> planLines = planService.getProductionLines(planProductId);

        if (planLines.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new Message(404, "해당 제품의 생산 라인 배정 데이터가 없습니다.", Map.of()));
        }

        return ResponseEntity.ok(new Message(200, "생산 라인 배정 데이터 조회 성공", Map.of("planLines", planLines)));
    }

    // 생산라인 배정 등록
    @PostMapping("/lines")
    public ResponseEntity<Message> createProductionLine(@RequestBody List<PlanLineDTO> planLineDTOs) {
        if (planLineDTOs == null || planLineDTOs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Message(400, "요청 데이터가 비어 있습니다.", Map.of()));
        }

        planService.saveProductionLines(planLineDTOs);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Message(201, "생산 라인 배정이 완료되었습니다.", Map.of()));
    }

    // Step3 실시간 자재 소요량 계산
    @PostMapping("/materials/calculate")
    public ResponseEntity<Message> calculateMaterials(
            @RequestBody ProductionPlanDTO productionPlanDTO
    ) {
        try {
            // 받은 데이터 로깅
            System.out.println("받은 제품 정보: " + productionPlanDTO);
            System.out.println("제품 목록: " + productionPlanDTO.getProducts());

            Map<String, Object> result = planService.calculateMaterialRequirements(productionPlanDTO);

            return ResponseEntity.ok(new Message(
                    200,
                    "자재 소요량 계산 완료",
                    result
            ));
        } catch (Exception e) {
            // 에러 로깅 추가
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message(
                            500,
                            "자재 소요량 계산 중 오류 발생: " + e.getMessage(),
                            Map.of()
                    ));
        }
    }

    // 최종 저장 (Step 1,2,3 모두)
    @PostMapping("/save")
    public ResponseEntity<Message> savePlanWithMaterials(@RequestBody ProductionPlanDTO completeProductionPlan) {
        try {
            System.out.println("🚀 [컨트롤러] 받은 요청 데이터: " + completeProductionPlan);

            // ✅ Step 3 데이터 확인 (자재 리스트)
            if (completeProductionPlan.getMaterials() == null) {
                System.out.println("❌ [컨트롤러] materials 데이터가 NULL입니다.");
            } else {
                System.out.println("🔍 [컨트롤러] 받은 materials 크기: " + completeProductionPlan.getMaterials().size());
                for (PlanMaterialDTO material : completeProductionPlan.getMaterials()) {
                    System.out.println("   - Material ID: " + material.getMaterialId() + ", Name: " + material.getMaterialName());
                }
            }

            // ✅ Step 4 데이터 확인 (자재 구매 신청)
            if (completeProductionPlan.getMaterialRequests() == null) {
                System.out.println("❌ [컨트롤러] materialRequests 데이터가 NULL입니다.");
            } else {
                System.out.println("🔍 [컨트롤러] 받은 materialRequests: " + completeProductionPlan.getMaterialRequests());
            }

            List<String> savedPlanIds = planService.saveCompletePlan(completeProductionPlan);

            return ResponseEntity.ok(new Message(201, "생산 계획이 성공적으로 저장되었습니다.", Map.of("planIds", savedPlanIds)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new Message(500, "생산 계획 저장 중 오류 발생: " + e.getMessage(), Map.of()));
        }
    }

    @GetMapping("detail/{planId}")
    @Operation(summary = "생산 계획 상세조회", description = "생산계획의 모든 상세정보를 조회합니다.",
            parameters = {@Parameter(name ="planId", description = "생산계획ID")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생산 계획 상세 조회 성공"),
            @ApiResponse(responseCode = "404", description = "생산 계획 정보가 없습니다.")
    })
    public ResponseEntity<Message> getProductionPlanDetail(@PathVariable String planId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        try {
            ProductionPlanDetailDTO planDetail = planService.getProductionPlanDetail(planId);

            // 불필요한 중첩 제거를 위한 맵 생성
            Map<String, Object> simplifiedPlanDetail = new HashMap<>();

            // planMst 직접 추가
            simplifiedPlanDetail.put("planMst", planDetail.getPlanMst());

            // planProducts 중복 참조 제거
            List<PlanProduct> simplifiedProducts = planDetail.getPlanProducts().stream()
                    .map(product -> {
                        // 중복된 planMst 참조 제거
                        product.setPlanMst(null);
                        return product;
                    })
                    .collect(Collectors.toList());
            simplifiedPlanDetail.put("planProducts", simplifiedProducts);

            // 다른 주요 속성들 추가
            simplifiedPlanDetail.put("planLines", planDetail.getPlanLines());
            simplifiedPlanDetail.put("processSteps", planDetail.getProcessSteps());
            simplifiedPlanDetail.put("productBeerTypes", planDetail.getProductBeerTypes());
            simplifiedPlanDetail.put("rawMaterials", planDetail.getRawMaterials());
            simplifiedPlanDetail.put("packagingMaterials", planDetail.getPackagingMaterials());

            // 최종 응답 생성
            Map<String, Object> res = new HashMap<>();
            res.put("planDetail", simplifiedPlanDetail);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new Message(200, "생산 계획 상세 조회 성공", res));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .body(new Message(404, "생산 계획 정보가 없습니다.", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(new Message(500, "서버 오류: " + e.getMessage(), null));
        }
    }

    @PostMapping("statusUpdate/{planId}")
    @Operation(summary = "생산 계획 상태 변경", description = "생산 계획의 상태를 변경합니다.",
            parameters = {
                    @Parameter(name = "planId", description = "생산 계획 ID", required = true),
                    @Parameter(name = "status", description = "변경할 상태 (예: 미확정, 확정)", required = true)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "생산 계획 상태 변경 성공!!"),
            @ApiResponse(responseCode = "404", description = "생산 계획 정보를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 상태 값"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류?")
    })
    public ResponseEntity<Message> updatePlanStatus(  @PathVariable @Parameter(description = "생산 계획 ID") String planId,
                                                      @RequestParam @Parameter(description = "변경할 상태") String status
    ){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        try {
            // 유효한 상태 값인지 검증쓰
            if (!"미확정".equals(status) && !"확정".equals(status)) {
                return ResponseEntity.badRequest()
                        .headers(headers)
                        .body(new Message(400, "유효하지 않은 상태 값입니다.", null));
            }
            // 상태 변경 서비스 호출
            planService.updatePlanStatus(planId, status);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new Message(200, "생산 계획 상태가 성공적으로 변경되었습니다.", null));

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .body(new Message(404, "생산 계획 정보를 찾을 수 없습니다.", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(new Message(500, "상태 변경 중 서버 오류 발생: " + e.getMessage(), null));
        }
    }

    // 자재 재고 현황 조회
    @GetMapping("/materials")
    @Operation(summary = "자재 재고 현황 조회", description = "모든 자재의 현재 재고 현황을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "자재 재고 현황 조회 성공")
    public ResponseEntity<Message> getStockStatus() {
        List<MaterialWarehouse> stockStatus = planService.getStockStatus();
        return ResponseEntity.ok(new Message(200, "자재 재고 현황 조회 성공", Map.of("stockStatus", stockStatus)));
    }


    // 자재 구매 신청 내역 조회
    @GetMapping("/materials/requests")
    @Operation(summary = "자재 구매 신청 내역 조회 (순환 참조 방지)", description = "순환 참조 없는 자재 구매 신청 내역을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "자재 구매 신청 내역 조회 성공")
    public ResponseEntity<Message> getMaterialRequests() {
        List<MaterialRequestDTO> requests = planService.getMaterialRequests();

        // ✅ 디버깅 로그 추가
        for (MaterialRequestDTO request : requests) {
            System.out.println("📌 요청 ID: " + request.getRequestId() + ", 자재명: " + request.getMaterialName());
        }


        return ResponseEntity.ok(new Message(200, "자재 구매 신청 내역 조회 성공", Map.of("requests", requests)));
    }


    // 자재 구매 신청
    @PostMapping("/materials/request")
    @Operation(summary = "자재 구매 신청", description = "자재 구매를 신청합니다.")
    @ApiResponse(responseCode = "201", description = "자재 구매 신청 성공")
    public ResponseEntity<Message> requestMaterial(@RequestBody MaterialRequestDTO requestDTO) {
        System.out.println("📌 [자재 구매 신청] 요청 데이터: " + requestDTO);

        MaterialRequest savedRequest = planService.requestMaterial(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new Message(201, "자재 구매 신청 성공", Map.of("request", savedRequest)));
    }


    @PutMapping("/materials/request/{requestId}/status")
    @Operation(summary = "자재 구매 신청 상태 변경", description = "자재 구매 신청 상태를 변경합니다.",
            parameters = {
                    @Parameter(name = "requestId", description = "자재 구매 신청 ID", required = true),
                    @Parameter(name = "status", description = "변경할 상태 (발주완료)", required = true)
            })
    @ApiResponses({
            @ApiResponse(responseCode = "200",description = "자재 구매 신청 상태 변경 성공!"),
            @ApiResponse(responseCode = "404",description = "해당 요청을 찾을수 없음"),
            @ApiResponse(responseCode = "400",description = "잘못된 상태값"),
            @ApiResponse(responseCode = "500",description = "서버 오류")
    })
    public ResponseEntity<Message> updateMaterialRequestStatus(
            @PathVariable String requestId,
            @RequestParam String status) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        try {
            // 유효한 상태 값인지 검증 (현재는 '발주완료'만 가능)
            if (!"발주완료".equals(status)) {
                return ResponseEntity.badRequest()
                        .headers(headers)
                        .body(new Message(400, "유효하지 않은 상태 값입니다. ('발주완료'만 허용)", null));
            }

            // 서비스 호출
            boolean updated = planService.updateMaterialRequestStatus(requestId, status);

            if (!updated) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .headers(headers)
                        .body(new Message(404, "해당 자재 구매 신청을 찾을 수 없습니다.", null));
            }

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new Message(200, "자재 구매 신청 상태가 '발주완료'로 변경되었습니다.", null));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(new Message(500, "자재 구매 신청 상태 변경 중 서버 오류 발생: " + e.getMessage(), null));
        }
    }



}
