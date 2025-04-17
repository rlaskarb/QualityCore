package com.org.qualitycore.standardinformation.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.FermentationTimedLogDTO;
import com.org.qualitycore.standardinformation.model.dto.MaterialGrindingDTO;
import com.org.qualitycore.standardinformation.model.entity.FermentationTimedLog;
import com.org.qualitycore.standardinformation.model.service.FermentationTimedLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/fermentationtimedlog")
@CrossOrigin(origins ="http://localhost:3000" )
@RequiredArgsConstructor
@Tag(name="FermentationTimedLog" , description = "발효 시간대별 공정 API")
@Slf4j
public class FermentationTimedLogController {


    private final FermentationTimedLogService fermentationTimedLogService;

    // ✅ 발효 상세 공정 ID 목록 조회
    @Operation(summary = "발효 상세 ID 목록 조회", description = "현재 등록된 발효 상세 공정 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/fermentation-ids")
    public ResponseEntity<Message> getFermentationIds() {
        log.info("컨트롤러: 발효 공정 ID 목록 조회 요청");

        List<String> fermentationIds = fermentationTimedLogService.findAllFermentationIds();

        Message response = new Message(200, "발효 공정 ID 목록 조회 성공", new HashMap<>());
        response.getResult().put("fermentationIds", fermentationIds);

        return ResponseEntity.ok(response);
    }




    // ✅ 발효 시간별 등록
    @Operation(summary = "발효 공정 로그 등록", description = "발효 공정의 시간대별 데이터를 기록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "발효 공정 로그 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/log")
    public ResponseEntity<Message> logFermentationData(@RequestBody FermentationTimedLogDTO request) {
        log.info("컨트롤러: 발효 공정 로그 등록 요청 - {}", request);

        FermentationTimedLog savedLog = fermentationTimedLogService.saveLog(request);

        Message response = new Message(201, "발효 공정 로그 등록 성공", new HashMap<>());
        response.getResult().put("logId", savedLog.getFlogId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    // 실제 종료시간을  위해서 수정 추가 구현
    @PutMapping("/update/{flogId}")
    public ResponseEntity<FermentationTimedLogDTO> completeEndTime(
            @PathVariable Long flogId) {
        log.info("컨트롤러 : 시간별 등록 공정 완료 요청 - ID: {}", flogId);
        FermentationTimedLogDTO updatedFermentationId = fermentationTimedLogService.completeEndTime(flogId);
        return ResponseEntity.ok(updatedFermentationId);
    }



}
