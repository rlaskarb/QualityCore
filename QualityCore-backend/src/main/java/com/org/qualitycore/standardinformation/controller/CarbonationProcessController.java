package com.org.qualitycore.standardinformation.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.*;
import com.org.qualitycore.standardinformation.model.service.BoilingProcessService;
import com.org.qualitycore.standardinformation.model.service.CarbonationProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/carbonationprocess")
@CrossOrigin(origins ="http://localhost:3000" )
@RequiredArgsConstructor
@Tag(name="CarbonationProcess" , description = "탄산 조정 공정 API")
@Slf4j
public class CarbonationProcessController {

    private final CarbonationProcessService carbonationProcessService;



    // ✅ 작업지시 ID 목록 조회
    @Operation(summary = "작업지시 ID 목록 조회", description = "현재 등록된 작업지시 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/linematerial")
    public ResponseEntity<Message> getLineMaterial() {
        log.info("컨트롤러: 작업지시 ID 목록 조회 요청");
        List<LineMaterialNDTO> lineMaterials = carbonationProcessService.getLineMaterial();

        Message response = new Message(200, "작업지시 ID 목록 조회 성공", new HashMap<>());
        response.getResult().put("lineMaterials", lineMaterials);

        return ResponseEntity.ok(response);
    }


    //탄산 조정 공정 등록
    @Operation(summary = "탄산 조정 공정" , description = "탄산 조정 공정 작업을 등록합니다")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201" , description = "등록에 성공!!"),
            @ApiResponse(responseCode = "400" , description = "잘못된 요청입니다.")
    })
    @PostMapping("/register")
    public ResponseEntity<Message> createCarbonationProcess(
            @RequestBody @Parameter(description = "등록할 탄산 조정 정보", required = true)
            CarbonationProcessDTO carbonationProcessDTO) {
        log.info("컨트롤러 : 탄산 조정 공정 등록 요청 {}", carbonationProcessDTO);
        Message response = carbonationProcessService.createCarbonationProcess(carbonationProcessDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }


    // 실제 종료시간을  위해서 수정 추가 구현
    @PutMapping("/update/{carbonationId}")
    public ResponseEntity<CarbonationProcessDTO> completeEndTime(
            @PathVariable String carbonationId) {
        log.info("컨트롤러 : 탄산 조정 공정 완료 요청 - ID: {}", carbonationId);
        CarbonationProcessDTO updatedCarbonation = carbonationProcessService.completeEndTime(carbonationId);
        return ResponseEntity.ok(updatedCarbonation);
    }


    // ✅ 특정 LOT_NO에 대한 탄산 조정 공정 상태 업데이트
    @Operation(summary = "LOT_NO에 따른 탄산 조정 공정 상태 업데이트", description = "LOT_NO를 기준으로 공정 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 LOT_NO 없음")
    })
    @PutMapping("/update")
    public ResponseEntity<Message> updateCarbonationProcessStatus
    (@RequestBody CarbonationProcessDTO carbonationProcessDTO) {

        log.info("컨트롤러: LOT_NO={} 탄산 조정 공정 상태 업데이트 요청 - 데이터: {}",
                carbonationProcessDTO.getLotNo(), carbonationProcessDTO);

        Message response = carbonationProcessService.updateCarbonationProcessStatus(carbonationProcessDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }


}
