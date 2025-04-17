package com.org.qualitycore.standardinformation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.qualitycore.common.CloudinaryService;
import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.EquipmentInfoDTO;
import com.org.qualitycore.standardinformation.model.service.EquipmentInfoService;
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
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class EquipmentInfoController {

    private final EquipmentInfoService equipmentInfoService;
    private final CloudinaryService cloudinaryService;

    // 설비 전체조회
    @GetMapping("/equipment")
    public ResponseEntity<Message> findEquipmentAll(@PageableDefault Pageable pageable,
                                                    @RequestParam(required = false) String searchType,
                                                    @RequestParam(required = false) String searchKeyword) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        Page<EquipmentInfoDTO> equipment = equipmentInfoService.findEquipmentAll(pageable, searchType, searchKeyword);

        Map<String, Object> res = new HashMap<>();
        res.put("equipment", equipment);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "전체조회성공", res));
    }

    // 설비 상세조회
    @GetMapping("/equipment/{equipmentId}")
    public ResponseEntity<Message> findByCodeEquipment(@PathVariable("equipmentId") String equipmentId) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(new MediaType("Application", "json", Charset.forName("UTF-8")));

        EquipmentInfoDTO equipment = equipmentInfoService.findByCodeEquipment(equipmentId);

        Map<String, Object> res = new HashMap<>();
        res.put("equipment", equipment);

        return ResponseEntity.ok().headers(headers).body(new Message(200, "상세조회성공", res));
    }

    // 설비등록
    @PostMapping(value = "/equipment", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEquipment(
            @RequestPart("equipmentData") String equipmentDataJson,  // JSON 문자열로 받음
            @RequestPart(value = "equipmentImage", required = false) MultipartFile equipmentImage) {

        ObjectMapper objectMapper = new ObjectMapper();
        EquipmentInfoDTO equipmentData;

        try {
            String decodedEquipmentDataJson = URLDecoder.decode(equipmentDataJson, StandardCharsets.UTF_8.name());
            equipmentData = objectMapper.readValue(decodedEquipmentDataJson, EquipmentInfoDTO.class); // JSON 문자열을 DTO로 변환

            // DTO의 각 필드 디코딩
            equipmentData.setEquipmentName(URLDecoder.decode(equipmentData.getEquipmentName(), StandardCharsets.UTF_8.name()));
            equipmentData.setModelName(URLDecoder.decode(equipmentData.getModelName(), StandardCharsets.UTF_8.name()));
            equipmentData.setManufacturer(URLDecoder.decode(equipmentData.getManufacturer(), StandardCharsets.UTF_8.name()));
            equipmentData.setEquipmentStatus(URLDecoder.decode(equipmentData.getEquipmentStatus(), StandardCharsets.UTF_8.name()));
            equipmentData.setEquipmentEtc(URLDecoder.decode(equipmentData.getEquipmentEtc(), StandardCharsets.UTF_8.name()));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new Message(400, "equipmentData 파싱 실패", null));
        }

        // 이미지 업로드 처리
        if (equipmentImage != null && !equipmentImage.isEmpty()) {
            try {
                String imageUrl = cloudinaryService.uploadImage(equipmentImage);
                equipmentData.setEquipmentImage(imageUrl);  // 이미지 URL을 equipmentData에 추가
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new Message(500, "이미지 업로드 실패", null));
            }
        }

        // 서비스 호출
        equipmentInfoService.createEquipment(equipmentData);

        // 응답 반환
        Map<String, Object> res = new HashMap<>();
        res.put("status", 201);
        res.put("message", "설비 등록 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 설비수정
    @PutMapping("/equipment")
    public ResponseEntity<?> updateEquipment(@RequestBody EquipmentInfoDTO equipment) {

        equipmentInfoService.updateEquipment(equipment);

        Map<String, Object> res = new HashMap<>();

        res.put("status", 201);

        res.put("message", "설비 수정 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    // 설비삭제
    @DeleteMapping("/equipment/{equipmentId}")
    public ResponseEntity<?> deleteEquipment(@PathVariable("equipmentId") String equipmentId) {

        equipmentInfoService.deleteEquipment(equipmentId);

        Map<String, Object> res = new HashMap<>();

        res.put("status", 201);

        res.put("message", "설비 삭제 성공");

        return ResponseEntity.status(HttpStatus.CREATED).body(res);

    }

}
