package com.org.qualitycore.standardinformation.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.PackagingAndShipmentDTO;
import com.org.qualitycore.standardinformation.model.service.PackagingAndShipmentService;
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
@RequestMapping("/packagingandshipment")
@CrossOrigin(origins ="http://localhost:3000" )
@RequiredArgsConstructor
@Tag(name="PackagingAndShipment" , description = "패키징 및 출하 공정 API")
@Slf4j
public class PackagingAndShipmentController {


    private final PackagingAndShipmentService packagingAndShipmentService;




    // ✅ 작업지시 ID 목록 조회
    @Operation(summary = "작업지시 ID 목록 조회", description = "현재 등록된 작업지시 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/linematerial")
    public ResponseEntity<Message> getLineMaterial() {
        log.info("컨트롤러: 작업지시 ID 목록 조회 요청");
        List<LineMaterialNDTO> lineMaterials = packagingAndShipmentService.getLineMaterial();

        Message response = new Message(200, "작업지시 ID 목록 조회 성공", new HashMap<>());
        response.getResult().put("lineMaterials", lineMaterials);

        return ResponseEntity.ok(response);
    }

    //패키징 및 출하공정 등록
    @Operation(summary = "패키징 및 출하공정" , description = "패키징 및 출하공정 작업을 등록합니다")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201" , description = "등록에 성공!!"),
            @ApiResponse(responseCode = "400" , description = "잘못된 요청입니다.")
    })
    @PostMapping("/register")
    public ResponseEntity<Message> createPackagingAndShipment(
            @RequestBody @Parameter(description = "등록할 패키징 및 출하 정보", required = true)
            PackagingAndShipmentDTO packagingAndShipmentDTO) {
        log.info("컨트롤러 : 패키징 및 출하공정 등록 요청 {}", packagingAndShipmentDTO);
        Message response = packagingAndShipmentService.createPackagingAndShipment(packagingAndShipmentDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // ✅ 특정 LOT_NO에 대한 패키징 및 출하 공정 상태 업데이트
    @Operation(summary = "LOT_NO에 따른 패키징 및 출하 공정 상태 업데이트", description = "LOT_NO를 기준으로 공정 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 LOT_NO 없음")
    })
    @PutMapping("/update")
    public ResponseEntity<Message> updatePackagingAndShipmentStatus
    (@RequestBody PackagingAndShipmentDTO packagingAndShipmentDTO) {

        log.info("컨트롤러: LOT_NO={} 패키징 및 출하 공정 상태 업데이트 요청 - 데이터: {}",
                packagingAndShipmentDTO.getLotNo(), packagingAndShipmentDTO);

        Message response = packagingAndShipmentService.updatePackagingAndShipmentStatus(packagingAndShipmentDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

}
