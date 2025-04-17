package com.org.qualitycore.standardinformation.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.FiltrationProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.MashingProcessDTO;
import com.org.qualitycore.standardinformation.model.service.FiltrationProcessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.util.Map;

@RestController
@RequestMapping("/filtrationprocess")
@CrossOrigin(origins ="http://localhost:3000" )
@RequiredArgsConstructor
@Tag(name= "FiltrationProcess" , description = "여과 공정 API")
@Slf4j
public class FiltrationProcessController {

    private final FiltrationProcessService filtrationProcessService;



    // 여과 공정 전체 조회
    @GetMapping("/all")
    @Operation(summary = "전체 여과 공정 조회", description = "모든 여과 공정 데이터를 조회합니다.")
    public ResponseEntity<Message> getAllFiltrationProcesses() {
        List<FiltrationProcessDTO> filtrationProcesses = filtrationProcessService.getAllFiltrationProcesses();
        Message response = new Message(200, "전체 여과 공정 조회 성공", Map.of("data", filtrationProcesses));
        return ResponseEntity.ok(response);
    }


    // 여과공정 상세 조회
    @GetMapping("/filtration/{lotNo}")
    @Operation(summary = "여과 공정 상세 조회", description = "특정 여과 공정 데이터를 lotNo를 기반으로 조회합니다.")
    public ResponseEntity<Message> getFiltrationProcessesByLotNo(@PathVariable String lotNo) {
        List<FiltrationProcessDTO> filtrationProcesses = filtrationProcessService.getFiltrationProcessesByLotNo(lotNo);
        if (!filtrationProcesses.isEmpty()) {
            Message response = new Message(200, "여과 공정 상세 조회 성공", Map.of("data", filtrationProcesses));
            return ResponseEntity.ok(response);
        } else {
            Message response = new Message(404, "해당 LotNo에 대한 여과 공정 데이터를 찾을 수 없습니다.", Map.of());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



    // ✅ 작업지시 ID 목록 조회
    @Operation(summary = "작업지시 ID 목록 조회", description = "현재 등록된 작업지시 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/linematerial")
    public ResponseEntity<Message> getLineMaterial() {
        log.info("컨트롤러: 작업지시 ID 목록 조회 요청");
        List<LineMaterialNDTO> lineMaterials = filtrationProcessService.getLineMaterial();

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
        log.info ("컨트롤러: 자재정보 LOT_NO={}에 대한 자재 정보 요청", lotNo);
        List<LineMaterialNDTO> materials = filtrationProcessService.getMaterialsByLotNo(lotNo);
        Message response;
        if (materials.isEmpty()) {
            response = new Message(404, "데이터 없음", new HashMap<>());
        } else {
            response = new Message(200, "작업지시 ID 로 특정 자재 정보를 조회성공", new HashMap<>());
            response.getResult().put("materials", materials);
        }
        return ResponseEntity.status(response.getCode()).body(response);
    }




    //여과공정 등록
    @Operation(summary = "여과공정" , description = "여과공정 작업을 등록합니다")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201" , description = "등록에 성공!!"),
            @ApiResponse(responseCode = "400" , description = "잘못된 요청입니다.")
    })
    @PostMapping("/register")
    public ResponseEntity<Message> createFiltrationProcess(
            @RequestBody @Parameter(description = "등록할 여과 정보", required = true)
            FiltrationProcessDTO filtrationProcessDTO) {
        log.info("컨트롤러 : 여과공정 등록 요청 {}", filtrationProcessDTO);
        Message response = filtrationProcessService.createFiltrationProcess(filtrationProcessDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }



    // 여과 공정 회수된 워트량 , 손실량 , 실제종료시간 수정 구문

    @Operation(
            summary = "여과 공정 업데이트",
            description = "주어진 ID의 여과 공정에서 회수된 워트량, 손실량 및 실제 종료 시간을 업데이트합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 업데이트됨",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다")})
    @PutMapping("/update/{filtrationId}")
    public ResponseEntity<Message> updateFiltrationProcess(
            @PathVariable @Parameter(description = "업데이트할 여과 공정의 ID", required = true) String filtrationId,
            @RequestBody @Parameter(description = "수정할 여과 공정 정보", required = true)
            Map<String, Object> requestBody) {
        log.info("컨트롤러 : 여과 공정 업데이트 요청 - ID {}, 요청 데이터 {}", filtrationId, requestBody);

        Object recoveredWortVolumeObj = requestBody.get("recoveredWortVolume");
        Double recoveredWortVolume = (recoveredWortVolumeObj instanceof Number number)
                ? number.doubleValue()
                : null;

        Object lossVolumeObj = requestBody.get("lossVolume");
        Double lossVolume = (lossVolumeObj instanceof Number number)
                ? number.doubleValue()
                : null;



        Message response = filtrationProcessService.updateFiltrationProcess(filtrationId, recoveredWortVolume, lossVolume);
        return ResponseEntity.status(response.getCode()).body(response);

    }



    // ✅ 특정 LOT_NO에 대한 여과 공정 상태 업데이트
    @Operation(summary = "LOT_NO에 따른 여과 공정 상태 업데이트", description = "LOT_NO를 기준으로 공정 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 LOT_NO 없음")
    })
    @PutMapping("/update")
    public ResponseEntity<Message> updateFiltrationProcessStatus
    (@RequestBody FiltrationProcessDTO filtrationProcessDTO) {

        log.info("컨트롤러: LOT_NO={} 여과 공정 상태 업데이트 요청 - 데이터: {}",
                filtrationProcessDTO.getLotNo(), filtrationProcessDTO);

        Message response = filtrationProcessService.updateFiltrationProcessStatus(filtrationProcessDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }






}






