package com.org.qualitycore.standardinformation.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.LabelInfoDTO;
import com.org.qualitycore.standardinformation.model.service.LabelInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class LabelInfoController {

    private final LabelInfoService labelInfoService;

    // 라벨전체조회
    @GetMapping("/labelInfo")
    public ResponseEntity<Message> findAllLabelInfo(@PageableDefault Pageable pageable,
                                                    @RequestParam (value = "search", required = false, defaultValue = "") String search) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        Page<LabelInfoDTO> labelInfo = labelInfoService.findAllLabelInfo(pageable, search);

        Map<String, Object> res = new HashMap<>();
        res.put("labelInfo", labelInfo);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "전체조회완료", res));
    }

    // 상세조회
    @GetMapping("/labelInfo/{labelId}")
    public ResponseEntity<Message> findByIdLabelInfo(@PathVariable("labelId") String labelId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        LabelInfoDTO labelInfo = labelInfoService.findByIdLabelInfo(labelId);

        Map<String, Object> res = new HashMap<>();
        res.put("labelInfo", labelInfo);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "상세조회완료", res));
    }

    // 등록
    @PostMapping(value = "/labelInfo", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<?> createLabelInfo(
            @RequestPart("labelData") String labelDataJson,
            @RequestPart(value = "labelImage", required = false) MultipartFile labelImage,
            @RequestPart(value = "beerImage", required = false) MultipartFile beerImage) {

        // JSON 문자열을 DTO로 변환
        ObjectMapper objectMapper = new ObjectMapper();
        LabelInfoDTO labelInfo;
        try {
            labelInfo = objectMapper.readValue(labelDataJson, LabelInfoDTO.class);

            // DTO의 각 필드 디코딩
            try {
                labelInfo.setBeerSupplier(URLDecoder.decode(labelInfo.getBeerSupplier(), StandardCharsets.UTF_8.name()));
            } catch (UnsupportedEncodingException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("디코딩 실패");
            }

        } catch (JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid JSON format");
        }

        // 서비스 호출
        labelInfoService.createLabelInfo(labelInfo, labelImage, beerImage);

        Map<String, Object> res = new HashMap<>();
        res.put("code", 201);
        res.put("message", "등록성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 삭제
    @DeleteMapping("/labelInfo/{labelId}")
    public ResponseEntity<?> deleteLabelInfo(@PathVariable("labelId") String labelId) {

        labelInfoService.deleteLabelInfo(labelId);

        Map<String, Object> res = new HashMap<>();
        res.put("code", 200);
        res.put("message", "삭제성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }
}