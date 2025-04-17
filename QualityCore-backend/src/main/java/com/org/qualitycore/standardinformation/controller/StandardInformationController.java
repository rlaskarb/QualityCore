package com.org.qualitycore.standardinformation.controller;

import com.org.qualitycore.standardinformation.model.dto.WorkplaceDTO;
import com.org.qualitycore.standardinformation.model.entity.ErpMessage;
import com.org.qualitycore.standardinformation.model.service.StandardInformationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/standardinformation")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
@Tag(name="Workplace" , description = "작업장정보 API")
@Slf4j
public class StandardInformationController {

    private final StandardInformationService standardInformationService;



    // 작업장 전체 조회
    @Operation(summary = "작업장정보 전체조회", description = "모든 작업장 정보를 전체 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작업장 목록 조회 성공.")})

    @GetMapping("/workplaces/find")
    public ResponseEntity<List<WorkplaceDTO>> getAllWorkplaces() {
        log.info("컨트롤러 : 작업장 전체 조회 요청!");
        List<WorkplaceDTO> workplaceDTO =standardInformationService.getAllWorkplaces();
            return ResponseEntity.ok(workplaceDTO);


    }


    // 작업장 등록
    @Operation(summary = "작업장정보 등록", description = "새로운 작업장을 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "작업장 등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")})

    @PostMapping("/workplaces/regist")
    public ResponseEntity<ErpMessage> createWorkplace(
            @RequestBody @Parameter(description = "등록할 작업장 정보", required = true)
            WorkplaceDTO workplaceDTO) {
        log.info("컨트롤러 : 작업장 등록 요청 {}" , workplaceDTO );
        ErpMessage response = standardInformationService.createWorkplace(workplaceDTO);
        return ResponseEntity.status(response.getHttpStatusCode())
                .body(response);
    }

    // 컨트롤러 예외 처리 추가!
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErpMessage> handleResponseStatusException(ResponseStatusException e) {
        log.error(" 컨트롤러: 예외 발생 - {}", e.getReason());
        return ResponseEntity.status(e.getStatusCode())
                .body(new ErpMessage(e.getStatusCode().value(), e.getReason()));
    }


    // 작업장 수정
    @Operation(summary = "작업장정보 수정", description = "장업장 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작업장 수정 성공!"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터입니다")})

    @PutMapping("/workplaces/{workplaceId}")
    public ResponseEntity<ErpMessage> updateWorkplace(
            @PathVariable @Parameter(description = "수정할 작업장의 ID", required = true) String workplaceId,
            @RequestBody @Parameter(description = "수정할 작업장 정보", required = true) WorkplaceDTO workplaceDTO) {
        log.info("컨트롤러 : 작업장 수정 요청 - ID {} ,DTO {} " , workplaceId,workplaceDTO);
        ErpMessage response = standardInformationService.updateWorkplace(workplaceId, workplaceDTO);
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }


    // 작업장 등록 삭제
    @Operation(summary = "작업장정보 삭제", description = "작업장정보를 삭제 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "작업장 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 ID의 작업장을 찾을 수 없음")})

    @DeleteMapping("/workplaces/{workplaceId}")
    public ResponseEntity<ErpMessage> deleteWorkplace(
            @PathVariable @Parameter(description = "삭제할 장업장의 ID", required = true) String workplaceId) {

        log.info("컨트롤러 : 작업장 삭제 요청 ID {} " , workplaceId);
        ErpMessage response = standardInformationService.deleteWorkplace(workplaceId);
        return ResponseEntity.status(response.getHttpStatusCode()).body(response);
    }

}















