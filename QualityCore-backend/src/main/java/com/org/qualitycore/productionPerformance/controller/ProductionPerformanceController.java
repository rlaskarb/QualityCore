package com.org.qualitycore.productionPerformance.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.exception.ResourceNotFoundException;
import com.org.qualitycore.productionPerformance.model.dto.ProductionPerformanceDTO;
import com.org.qualitycore.productionPerformance.model.service.ProductionPerformanceService;
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
import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "ProductionPerformance", description = "생산실적 API_Controller")
public class ProductionPerformanceController {

    private final ProductionPerformanceService productionPerformanceService;

    // 응답 헤더 기본 설정
    private HttpHeaders getDefaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));
        return headers;
    }

    // 월별 생산실적 조회
    @GetMapping("/performance/monthly")
    public ResponseEntity<Message> getMonthlyPerformance(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") String yearMonth,
            @RequestParam(required = false) String productName) {

        HttpHeaders headers = getDefaultHeaders();

        try {
            System.out.println("컨트롤러: 월별 실적 조회 시작 - " + yearMonth + ", " + productName);
            YearMonth ym = YearMonth.parse(yearMonth);
            System.out.println("변환된 YearMonth: " + ym + ", 시작일: " + ym.atDay(1) + ", 종료일: " + ym.atEndOfMonth());

            List<ProductionPerformanceDTO> data = productionPerformanceService.getMonthlyPerformance(ym, productName);
            System.out.println("서비스 호출 완료, 데이터 크기: " + (data != null ? data.size() : "null"));

            Map<String, Object> res = new HashMap<>();
            res.put("monthlyData", data);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new Message(200, "월별 생산실적 조회 성공", res));
        } catch (Exception e) {
            System.out.println("======= 컨트롤러 오류 발생 =======");
            System.out.println("오류 유형: " + e.getClass().getName());
            System.out.println("오류 메시지: " + e.getMessage());
            e.printStackTrace(); // 상세 스택 트레이스 출력

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(new Message(500, "서버 오류: " + e.getMessage(), null));
        }
    }

    // 계획 대비 실적 조회
    @GetMapping("/performance/plan-vs-actual")
    @Operation(summary = "계획 대비 실적 조회", description = "계획 대비 실적 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "계획 대비 실적 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Message> getPlanVsActual(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") String yearMonth,
            @RequestParam(required = false) String productName) {

        HttpHeaders headers = getDefaultHeaders();

        try {
            YearMonth ym = YearMonth.parse(yearMonth);
            List<Map<String, Object>> data = productionPerformanceService.getPlanVsActual(ym, productName);

            Map<String, Object> res = new HashMap<>();
            res.put("planVsActual", data);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new Message(200, "계획 대비 실적 조회 성공", res));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(new Message(500, "서버 오류: " + e.getMessage(), null));
        }
    }

    // 제품별 효율성 조회
    @GetMapping("/performance/efficiency")
    @Operation(summary = "제품별 생산 효율성 조회", description = "제품별 생산 효율성 데이터를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제품별 효율성 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<Message> getProductEfficiency() {
        HttpHeaders headers = getDefaultHeaders();

        try {
            List<Map<String, Object>> data = productionPerformanceService.getProductEfficiency();

            Map<String, Object> res = new HashMap<>();
            res.put("efficiency", data);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(new Message(200, "제품별 효율성 조회 성공", res));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .headers(headers)
                    .body(new Message(500, "서버 오류: " + e.getMessage(), null));
        }
    }

    // Excel 다운로드
    @GetMapping("/performance/export/{reportType}")
    @Operation(summary = "실적 보고서 Excel 다운로드", description = "선택한 보고서 유형의 Excel 파일을 다운로드합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Excel 파일 다운로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    public ResponseEntity<byte[]> exportReport(
            @PathVariable String reportType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM") String yearMonth,
            @RequestParam(required = false) String productName) {

        try {
            byte[] excelBytes = productionPerformanceService.generateExcelReport(reportType, yearMonth, productName);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", reportType + "_report.xlsx");

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}