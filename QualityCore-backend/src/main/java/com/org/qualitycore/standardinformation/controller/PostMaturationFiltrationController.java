package com.org.qualitycore.standardinformation.controller;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.BoilingProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.PostMaturationFiltrationDTO;
import com.org.qualitycore.standardinformation.model.service.PostMaturationFiltrationService;
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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/postmaturationfiltration")
@CrossOrigin(origins ="http://localhost:3000" )
@RequiredArgsConstructor
@Tag(name="PostMaturationFiltration" , description = "숙성 후 여과 공정 API")
@Slf4j
public class PostMaturationFiltrationController {

    private final PostMaturationFiltrationService postMaturationFiltrationService;


    // ✅ 작업지시 ID 목록 조회
    @Operation(summary = "작업지시 ID 목록 조회", description = "현재 등록된 작업지시 ID 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류")
    })
    @GetMapping("/linematerial")
    public ResponseEntity<Message> getLineMaterial() {
        log.info("컨트롤러: 작업지시 ID 목록 조회 요청");
        List<LineMaterialNDTO> lineMaterials = postMaturationFiltrationService.getLineMaterial();

        Message response = new Message(200, "작업지시 ID 목록 조회 성공", new HashMap<>());
        response.getResult().put("lineMaterials", lineMaterials);

        return ResponseEntity.ok(response);
    }



    //숙성 후 여과 공정 등록
    @Operation(summary = "숙성 후 여과 공정" , description = "숙성 후 여과 공정 작업을 등록합니다")
    @ApiResponses(value= {
            @ApiResponse(responseCode = "201" , description = "등록에 성공!!"),
            @ApiResponse(responseCode = "400" , description = "잘못된 요청입니다.")
    })
    @PostMapping("/register")
    public ResponseEntity<Message> createPostMaturationFiltration(
            @RequestBody @Parameter(description = "등록할 숙성 후 여과 정보", required = true)
            PostMaturationFiltrationDTO postMaturationFiltrationDTO) {
        log.info("컨트롤러 : 숙성 후 여과 공정 등록 요청 {}", postMaturationFiltrationDTO);
        Message response = postMaturationFiltrationService.createPostMaturationFiltration(postMaturationFiltrationDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }



    // 숙성 후 여과 탁도 및 실제종료시간 업데이트
    @Operation(summary = "숙성 후 여과 공정 업데이트",
            description = "주어진 ID의 숙성 후 여과 공정에서 탁도 및 실제 종료 시간을 업데이트합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 업데이트됨",
                    content = @Content(schema = @Schema(implementation = Message.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다")})
    @PutMapping("/update/{mfiltrationId}")
    public ResponseEntity<Message> updatePostMaturationFiltration(
            @PathVariable @Parameter(description = "업데이트할 숙성 후 여과 공정의 ID", required = true) String mfiltrationId,
            @RequestBody @Parameter(description = "수정할 숙성 후 여과 공정 정보", required = true)
            Map<String, Object> requestBody) {
        log.info("컨트롤러 : 숙성 후 여과 공정 업데이트 요청 - ID {}, 요청 데이터 {}", mfiltrationId, requestBody);

        Object turbidityObj = requestBody.get("turbidity");
        Double turbidity = (turbidityObj instanceof Number number)
                ? number.doubleValue()
                : null;

        Object actualEndTimeObj = requestBody.get("actualEndTime");
        LocalDateTime actualEndTime = (actualEndTimeObj instanceof String str)
                ? LocalDateTime.parse(str)
                : null;

        Message response = postMaturationFiltrationService.updatePostMaturationFiltration(mfiltrationId, turbidity, actualEndTime);
        return ResponseEntity.status(response.getCode()).body(response);

    }


    // ✅ 특정 LOT_NO에 대한 숙성 후 여과 공정 상태 업데이트
    @Operation(summary = "LOT_NO에 따른 숙성 후 여과 공정 상태 업데이트", description = "LOT_NO를 기준으로 공정 상태를 업데이트합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "업데이트 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
            @ApiResponse(responseCode = "404", description = "해당 LOT_NO 없음")
    })
    @PutMapping("/update")
    public ResponseEntity<Message> updatePostMaturationFiltrationStatus
    (@RequestBody PostMaturationFiltrationDTO postMaturationFiltrationDTO) {

        log.info("컨트롤러: LOT_NO={} 숙성 후 여과 공정 상태 업데이트 요청 - 데이터: {}",
                postMaturationFiltrationDTO.getLotNo(), postMaturationFiltrationDTO);

        Message response = postMaturationFiltrationService.updatePostMaturationFiltrationStatus(postMaturationFiltrationDTO);
        return ResponseEntity.status(response.getCode()).body(response);
    }


}
