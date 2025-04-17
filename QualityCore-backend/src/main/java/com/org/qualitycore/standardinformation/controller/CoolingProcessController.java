package com.org.qualitycore.standardinformation.controller;


import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.BoilingProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.CoolingProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.service.CoolingProcessService;
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
@RequestMapping("/coolingprocess")
@CrossOrigin(origins ="http://localhost:3000" )
@RequiredArgsConstructor
@Tag(name="CoolingProcess" , description = "냉각 공정 API")
@Slf4j
public class CoolingProcessController {

    private final CoolingProcessService coolingProcessService;

    // ✅ 작업지시 ID 목록 조회
    @Operation(summary = "작업지시 ID 목록 조회", description = "현재 등록된 작업지시 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/linematerial")
    public ResponseEntity<Message> getLineMaterial() {
        log.info("컨트롤러: 작업지시 ID 목록 조회 요청");
        List<LineMaterialNDTO> lineMaterials = coolingProcessService.getLineMaterial();

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
    @GetMapping("/status/{lotNo}")
    public ResponseEntity<Message> getMaterialsByLotNo(@PathVariable String lotNo) {
        log.info ("컨트롤러: 냉각 자재정보 LOT_NO={}에 대한 자재 정보 요청", lotNo);
        List<LineMaterialNDTO> materials = coolingProcessService.getMaterialsByLotNo(lotNo);
        Message response;
        if (materials.isEmpty()) {
            response = new Message(404, "데이터 없음", new HashMap<>());
        } else {
            response = new Message(200, "작업지시 ID 로 특정 자재 정보를 조회성공", new HashMap<>());
            response.getResult().put("materials", materials);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }



    //냉각공정 등록
    @Operation(summary = "냉각공정" , description = "냉각공정 작업을 등록합니다")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201" , description = "등록에 성공!!"),
            @ApiResponse(responseCode = "400" , description = "잘못된 요청입니다.")
    })
    @PostMapping("/register")
    public ResponseEntity<Message> createCoolingProcess(
            @RequestBody @Parameter(description = "등록할 냉각 정보", required = true)
            CoolingProcessDTO coolingProcessDTO) {
        log.info("컨트롤러 : 냉각공정 등록 요청 {}", coolingProcessDTO);
        Message response = coolingProcessService.createCoolingProcess(coolingProcessDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }



    // 실제 종료시간을  위해서 수정 추가 구현
    @PutMapping("/update/{coolingId}")
    public ResponseEntity<CoolingProcessDTO> completeEndTime(
            @PathVariable String coolingId) {
        log.info("컨트롤러 : 냉각 공정 완료 요청 - ID: {}", coolingId);
        CoolingProcessDTO updatedCooling = coolingProcessService.completeEndTime(coolingId);
        return ResponseEntity.ok(updatedCooling);
    }



    // ✅ 특정 LOT_NO에 대한 냉각 공정 상태 업데이트
    @Operation(summary = "LOT_NO에 따른 냉각 공정 상태 업데이트", description = "LOT_NO를 기준으로 공정 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 LOT_NO 없음")
    })
    @PutMapping("/update")
    public ResponseEntity<Message> updateCoolingProcessStatus
    (@RequestBody CoolingProcessDTO coolingProcessDTO) {

        log.info("컨트롤러: LOT_NO={} 냉각 공정 상태 업데이트 요청 - 데이터: {}",
                coolingProcessDTO.getLotNo(), coolingProcessDTO);

        Message response = coolingProcessService.updateCoolingProcessStatus(coolingProcessDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
