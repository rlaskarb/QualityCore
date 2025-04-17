package com.org.qualitycore.work.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.exception.ResourceNotFoundException;
import com.org.qualitycore.work.model.dto.*;
import com.org.qualitycore.work.model.service.WorkService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "WorkOrder(작업지시서)", description = "작업지시서 API_Controller")
public class WorkController {

    private final WorkService workService;
    private final SimpMessagingTemplate messagingTemplate;

    // 맥주 랭킹차트
    @GetMapping("/beer-podium")
    public ResponseEntity<?> getBeerRanking() {
        try {
            List<BeerRankingDTO> ranking = workService.getTop3BeerRanking();

            if (ranking.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(Map.of("message", "이번 달 맥주 생산 데이터가 없습니다."));
            }

            return ResponseEntity.ok(ranking);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "맥주 랭킹 조회 중 오류 발생", "details", e.getMessage()));
        }
    }

    // 작업지시서 전체 조회
    @GetMapping("/work")
    @Operation(summary = "작업지시서 전체 조회", description = "작업지시서를 전체 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업지시서 전체조회 성공"),
            @ApiResponse(responseCode = "404", description = "작업지시서가 없습니다.")})
    public ResponseEntity<Message> findAllWorkOrders(@PageableDefault Pageable pageable,
                                                     @RequestParam(required = false) String workTeam,
                                                     @RequestParam(required = false) String productName,
                                                     @RequestParam(required = false) String lotNo,
                                                     @RequestParam(required = false) String lineNo,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                     @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        Page<WorkFindAllDTO> work = workService.findAllWorkOrders(pageable, workTeam, productName, lotNo, lineNo, startDate, endDate);

        // 작업지시서가 없을경우
        if (work == null || work.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .body(new Message(404, "작업지시서가 없습니다.", null));
        }
//
        // 전체 조회 성공 시
        Map<String, Object> res = new HashMap<>();
        res.put("work", work);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new Message(200, "작업지시서 전체조회 성공", res));
    }

    // 작업지시서 상세 조회
    @GetMapping("/work/{lotNo}")
    @Operation(summary = "작업지시서 상세 조회", description = "작업지시서 상세 조회합니다.",
            parameters = {@Parameter(name = "lotNo", description = "특정 작업지시서를 조회할 고유 PK")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업지시서 상세조회 성공"),
            @ApiResponse(responseCode = "404", description = "작업지시서가 없습니다.")})
    public ResponseEntity<Message> findByCodeWorkOrder(@PathVariable("lotNo") String lotNo) {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        // 작업지시서가 있을 경우 상세 조회
        WorkFindAllDTO work = workService.findByCodeWorkOrder(lotNo);

        // 작업지시서가 없을경우
        if (work == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .headers(headers)
                    .body(new Message(404, "작업지시서가 없습니다.", null));
        }

        Map<String, Object> res = new HashMap<>();

        res.put("work", work);

        return ResponseEntity.ok()
                .headers(headers)
                .body(new Message(200, "작업지시서 상세조회 성공", res));
    }

    // 작업지시서 등록
    @PostMapping("/work")
    @Operation(summary = "작업지시서 등록", description = "작업지시서를 등록합니다.",
            parameters = {@Parameter(name = "WorkFindAllDTO", description = "작업지시서 등록에 필요한 DTO")})
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "작업지시서 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")})
    public ResponseEntity<?> workOrderCreate(@RequestBody WorkFindAllDTO work) {
        try {

            workService.createWorkOrder(work);

            Map<String, Object> notification = new HashMap<>();
            notification.put("type", "WORK_ORDER");
            notification.put("message", "새 작업지시서가 등록되었습니다.");
            messagingTemplate.convertAndSend("/topic/workOrders", notification);

            Map<String, Object> res = new HashMap<>();
            res.put("status", 201);
            res.put("message", "작업지시서 생성 성공");

            return ResponseEntity.status(HttpStatus.CREATED).body(res);
        } catch (ResourceNotFoundException e) {
            // 잘못된 요청된 데이터, 필수값 누락
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 400);
            errorResponse.put("message", "잘못된 요청입니다. 데이터를 확인해주세요.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    // 생산계획에 포함된 작업지시서 데이터로 insert 할때 필요함
    @GetMapping("/planInfo")
    public ResponseEntity<Message> workOrderPlanInfo () {

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        List<PlanInfoDTO> planInfo = workService.workOrderPlanInfo();

        Map<String, Object> res = new HashMap<>();
        res.put("planInfo", planInfo);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "조회성공", res));
    }

    // 맥주레시피
    @GetMapping("/beerRecipes")
    public ResponseEntity<Message> beerRecipes() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("application", "json", Charset.forName("UTF-8")));

        Map<String, Map<String, List<BeerRecipesDTO>>> groupedBeerRecipes = workService.beerRecipes();

        Map<String, Object> res = new HashMap<>();
        res.put("beerRecipe", groupedBeerRecipes);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "조회 성공", res));
    }

    // 작업지시서 삭제
    @DeleteMapping("/work/{lotNo}")
    @Operation(summary = "작업지시서 삭제", description = "작업지시서를 삭제합니다.",
            parameters = {@Parameter(name = "lotNo", description = "특정 작업지시서를 한 개를 조회하여 삭제")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "작업지시서 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "작업지시서가 없습니다.")})
    public ResponseEntity<?> workOrderDelete(@PathVariable("lotNo") String lotNo) {
        try {

            workService.workOrderDelete(lotNo);

            // 삭제 성공 응답
            Map<String, Object> res = new HashMap<>();
            res.put("status", 200);
            res.put("message", "작업지시서 삭제 성공");

            return ResponseEntity.status(HttpStatus.OK).body(res);

        } catch (ResourceNotFoundException e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", 404);
            errorResponse.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }
    }
}
