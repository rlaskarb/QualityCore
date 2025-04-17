package com.org.qualitycore.standardinformation.controller;


import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.*;
import com.org.qualitycore.standardinformation.model.service.MaturationDetailsService;
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
import java.util.Map;


@RestController
@RequestMapping("/maturationdetails")
@CrossOrigin(origins ="http://localhost:3000" )
@RequiredArgsConstructor
@Tag(name="MaturationDetails" , description = "숙성 상세 공정 API")
@Slf4j
public class MaturationDetailsController {


    private final MaturationDetailsService maturationDetailsService;



    // ✅ 작업지시 ID 목록 조회
    @Operation(summary = "작업지시 ID 목록 조회", description = "현재 등록된 작업지시 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/linematerial")
    public ResponseEntity<Message> getLineMaterial() {
        log.info("컨트롤러: 작업지시 ID 목록 조회 요청");
        List<LineMaterialNDTO> lineMaterials = maturationDetailsService.getLineMaterial();

        Message response = new Message(200, "작업지시 ID 목록 조회 성공", new HashMap<>());
        response.getResult().put("lineMaterials", lineMaterials);

        return ResponseEntity.ok(response);
    }



    //숙성 상세 공정 등록
    @Operation(summary = "발효 상세 공정" , description = "발효 상세 공정 작업을 등록합니다")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201" , description = "등록에 성공!!"),
            @ApiResponse(responseCode = "400" , description = "잘못된 요청입니다.")
    })
    @PostMapping("/register")
    public ResponseEntity<Message> createMaturationDetails(
            @RequestBody @Parameter(description = "등록할 발효 상세 정보", required = true)
            MaturationDetailsDTO maturationDetailsDTO) {
        log.info("컨트롤러 : 숙성 상세 공정 등록 요청 {}", maturationDetailsDTO);
        Message response = maturationDetailsService.createMaturationDetails(maturationDetailsDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // 실제 종료시간을  위해서 수정 추가 구현
    @PutMapping("/update/{maturationId}")
    public ResponseEntity<MaturationDetailsDTO> completeEndTime(
            @PathVariable String  maturationId) {
        log.info("컨트롤러 : 시간별 등록 공정 완료 요청 - ID: {}", maturationId);
        MaturationDetailsDTO updatedMaturationId = maturationDetailsService.completeEndTime(maturationId);
        return ResponseEntity.ok(updatedMaturationId);
    }

    // ✅ 특정 LOT_NO에 대한 숙성 상세 공정 상태 업데이트
    @Operation(summary = "LOT_NO에 따른 숙성 상세 공정 상태 업데이트", description = "LOT_NO를 기준으로 공정 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 LOT_NO 없음")
    })
    @PutMapping("/update")
    public ResponseEntity<Message> updateMaturationDetailsStatus
    (@RequestBody MaturationDetailsDTO maturationDetailsDTO) {

        log.info("컨트롤러: LOT_NO={} 숙성 상세 공정 상태 업데이트 요청 - 데이터: {}",
                maturationDetailsDTO.getLotNo(), maturationDetailsDTO);

        Message response = maturationDetailsService
                .updateMaturationDetailsStatus(maturationDetailsDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }

    // ✅  ** 숙성 시간대별 전체 조회 ** 평균 할때 필요한거
    @Operation(summary = "숙성 시간대별 전체 데이터 조회", description = "maturationId가 없으면 전체 데이터를 조회하고, 있으면 해당 ID의 데이터를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/timed-logs")
    public ResponseEntity<Message> getAllTimedLogs(@RequestParam(name = "maturationId", required = false) String maturationId) {
        log.info("요청 받은 maturationId: {}", maturationId);

        List<MaturationTimedLogDTO> logs;

        if (maturationId == null || maturationId.isEmpty()) {
            // ✅ `maturationId`가 없으면 전체 데이터 조회
            logs = maturationDetailsService.getAllTimedLogsWithoutId();
        } else {
            // ✅ `maturationId`가 있으면 특정 발효 ID 데이터 조회
            logs = maturationDetailsService.getAllTimedLogsById(maturationId);
        }

        Message response = new Message(200, "숙성 시간대별 전체 데이터 조회 성공", new HashMap<>());
        response.getResult().put("logs", logs);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/maturation")
    public ResponseEntity<Message> findAllMaturation() {

        List<MaturationDetailsDTO> maturationDetail = maturationDetailsService.findAllMaturation();

        Map<String, Object> res = new HashMap<>();

        res.put("maturationDetail", maturationDetail);

        return ResponseEntity.ok().body(new Message(200, "전체조회", res));
    }

    @GetMapping("/maturation/{maturationId}")
    public ResponseEntity<Message> findByCodeMaturation(@PathVariable("maturationId") String maturationId) {

        MaturationDetailsDTO maturationDetail = maturationDetailsService.findByMaturationId(maturationId);

        Map<String, Object> res = new HashMap<>();

        res.put("maturationDetail", maturationDetail);

        return ResponseEntity.ok().body(new Message(200, "상세조회", res));
    }

}
