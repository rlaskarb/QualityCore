package com.org.qualitycore.standardinformation.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.MashingProcessDTO;
import com.org.qualitycore.standardinformation.model.service.MashingProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mashingprocess")
@CrossOrigin(origins ="http://localhost:3000" )
@RequiredArgsConstructor
@Tag(name="MashingProcess" , description = "당화 공정 API")
@Slf4j
public class MashingProcessController {

    private final MashingProcessService mashingProcessService;




    // ✅ 작업지시 ID 목록 조회
    @Operation(summary = "작업지시 ID 목록 조회", description = "현재 등록된 작업지시 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/linematerial")
    public ResponseEntity<Message> getLineMaterial() {
        log.info("컨트롤러: 작업지시 ID 목록 조회 요청");
        List<LineMaterialNDTO> lineMaterials = mashingProcessService.getLineMaterial();

        Message response = new Message(200, "작업지시 ID 목록 조회 성공", new HashMap<>());
        response.getResult().put("lineMaterials", lineMaterials);

        return ResponseEntity.ok(response);
    }


    // ✅ 특정 LOT_NO에 대한 자재 정보 조회
    @Operation(summary = "LOT_NO에 따른 자재 정보 조회", description = "작업지시 ID 로 특정 자재 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "데이터 없음")
    })
    @GetMapping("/{lotNo}")
    public ResponseEntity<Message> getMaterialsByLotNo(@PathVariable String lotNo) {
        log.info ("컨트롤러: LOT_NO={}에 대한 자재 정보 요청", lotNo);
        List<LineMaterialNDTO> materials = mashingProcessService.getMaterialsByLotNo(lotNo);
        Message response;
        if (materials.isEmpty()) {
            response = new Message(404, "데이터 없음", new HashMap<>());
        } else {
            response = new Message(200, "작업지시 ID 로 특정 자재 정보를 조회성공", new HashMap<>());
            response.getResult().put("materials", materials);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }





    //당화공정 등록
    @Operation(summary = "당화공정" , description = "당화공정 작업을 등록합니다")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201" , description = "등록에 성공!!"),
            @ApiResponse(responseCode = "400" , description = "잘못된 요청입니다.")
    })
    @PostMapping("/register")
    public ResponseEntity<Message> createMashingProcess(
            @RequestBody @Parameter(description = "등록할 당화 정보", required = true)
                MashingProcessDTO mashingProcessDTO) {
        log.info("컨트롤러 : 당화공정 등록 요청 {}", mashingProcessDTO);
        Message response = mashingProcessService.createMashingProcess(mashingProcessDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }





    // 당화공정 pH 값 및 실제종료시간 업데이트
    @Operation(
            summary = "당화 공정 완료",
            description = "주어진 ID의 당화 공정을 완료하고 pH 값을 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 공정이 완료됨",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다")})
    @PutMapping("/update/{mashingId}")
    public ResponseEntity<Message> completeMashingProcess(
            @PathVariable @Parameter(description = "완료할 당화 공정의 ID", required = true) String mashingId,
            @RequestBody @Parameter(description = "수정할 당화 공정 정보 (pH 값 포함)", required = true)
            Map<String, Object> requestBody) {
        log.info("컨트롤러 : 당화 공정 완료 요청 - ID {} , 요청 데이터 {} ", mashingId, requestBody);

        Object phValueObj = requestBody.get("phValue");
        Double phValue = (phValueObj instanceof Number number)
                ? number.doubleValue()
                :null;

        Message response = mashingProcessService.completeMashingProcess(mashingId, phValue);
        return ResponseEntity.status(response.getCode()).body(response);
    }




    // ✅ 특정 LOT_NO에 대한 분쇄 공정 상태 업데이트
    @Operation(summary = "LOT_NO에 따른 공정 상태 업데이트", description = "LOT_NO를 기준으로 공정 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 LOT_NO 없음")
    })
    @PutMapping("/update")
    public ResponseEntity<Message> updateMashingProcess
            (@RequestBody MashingProcessDTO mashingProcessDTO) {

        log.info("컨트롤러: LOT_NO={} 공정 상태 업데이트 요청 - 데이터: {}",
                            mashingProcessDTO.getLotNo(), mashingProcessDTO);

        Message response = mashingProcessService.updateMashingProcess(mashingProcessDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }


}
