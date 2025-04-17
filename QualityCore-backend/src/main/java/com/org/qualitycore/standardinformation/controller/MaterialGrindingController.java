package com.org.qualitycore.standardinformation.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.MaterialGrindingDTO;
import com.org.qualitycore.standardinformation.model.service.MaterialGrindingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@RequestMapping("/productionprocess")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name="ProductionProcess" , description = "생산공정 API")
@Slf4j
public class MaterialGrindingController {

        private final MaterialGrindingService materialGrindingService;




        // ✅ 작업지시 ID 목록 조회
    @Operation(summary = "작업지시 ID 목록 조회", description = "현재 등록된 작업지시 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/linematerial")
    public ResponseEntity<Message> getLineMaterial() {
        log.info("컨트롤러: 작업지시 ID 목록 조회 요청");
        List<LineMaterialNDTO> lineMaterials = materialGrindingService.getLineMaterial();
        Message response;
        if (lineMaterials.isEmpty()) {
            response = new Message(500, "서버 내부 오류", new HashMap<>());
        } else {
            response = new Message(200, "조회 성공", new HashMap<>());
            response.getResult().put("lineMaterials", lineMaterials);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }



    // ✅ 특정 LOT_NO에 대한 자재 정보 조회
    @Operation(summary = "LOT_NO에 따른 자재 정보 조회", description = "특정 작업지시 ID(LOT_NO)에 대한 자재 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "데이터 없음")
    })
    @GetMapping("/{lotNo}")
    public ResponseEntity<Message> getMaterialsByLotNo(@PathVariable String lotNo) {
        log.info("컨트롤러: LOT_NO={}에 대한 자재 정보 요청", lotNo);
        List<LineMaterialNDTO> materials = materialGrindingService.getMaterialsByLotNo(lotNo);
        Message response;
        if (materials.isEmpty()) {
            response = new Message(404, "데이터 없음", new HashMap<>());
        } else {
            response = new Message(200, "조회 성공", new HashMap<>());
            response.getResult().put("materials", materials);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }


    // ✅ 분쇄공정에 등록된 LOT_NO 조회
    @Operation(summary = "분쇄공정 조회", description = "분쇄공정에 등록된 작업지시 ID를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "등록된 데이터 없음")
    })
    @GetMapping("/materialgrinding/{lotNo}")
    public ResponseEntity<Message> getMaterialGrindingByLotNo(
            @PathVariable("lotNo") String lotNo) {
        log.info("컨트롤러: 분쇄공정 조회 요청 - lotNo: {}", lotNo);

        Message response = materialGrindingService.getMaterialGrindingByLotNo(lotNo);

        if (response.getResult() == null || response.getResult().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }

     //✅ 모든 분쇄공정 데이터 조회 (추가된 기능)
    @GetMapping("/materialgrinding")
    public ResponseEntity<Message> getAllMaterialGrinding() {
        log.info("컨트롤러: 모든 분쇄공정 데이터 조회 요청");

        Message response = materialGrindingService.getAllMaterialGrinding();

        if (response.getResult() == null || response.getResult().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        return ResponseEntity.ok(response);
    }




    //✅ 분쇄공정 등록
    @Operation(summary = "분쇄공정" ,description = "분쇄공정 작업을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201" , description = "등록에 성공!"),
            @ApiResponse(responseCode = "400" , description = "잘못된 요청입니다!")
    })
    @PostMapping("/materialgrinding")
    public ResponseEntity<Message> createMaterialGrinding(
            @RequestBody @Parameter(description = "등록할 분쇄 정보" ,
                    required = true) MaterialGrindingDTO materialGrindingDTO){
        log.info("컨트롤러 : 분쇄공정 등록 요청 {} " ,materialGrindingDTO);
        Message response = materialGrindingService.createMaterialGrinding(materialGrindingDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }



    // 실제 종료시간을  위해서 수정 추가 구현
    @PutMapping("/materialgrinding/{grindingId}")
    public ResponseEntity<MaterialGrindingDTO> completeEndTime(
            @PathVariable String grindingId) {
            log.info("컨트롤러 : 분쇄 공정 완료 요청 - ID: {}", grindingId);
            MaterialGrindingDTO updatedGrinding = materialGrindingService.completeEndTime(grindingId);
            return ResponseEntity.ok(updatedGrinding);
        }



        // ✅ 특정 LOT_NO에 대한 분쇄 공정 상태 업데이트
    @Operation(summary = "LOT_NO에 따른 공정 상태 업데이트", description = "LOT_NO를 기준으로 공정 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 LOT_NO 없음")
    })
    @PutMapping("/update")
    public ResponseEntity<Message> updateMaterialGrinding(@RequestBody MaterialGrindingDTO materialGrindingDTO) {
        log.info("컨트롤러: LOT_NO={} 공정 상태 업데이트 요청", materialGrindingDTO.getLotNo());
        Message response = materialGrindingService.updateMaterialGrinding(materialGrindingDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }




}



