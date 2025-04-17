package com.org.qualitycore.standardinformation.model.service;

import com.org.qualitycore.standardinformation.model.dto.FermentationTimedLogDTO;
import com.org.qualitycore.standardinformation.model.dto.MaterialGrindingDTO;
import com.org.qualitycore.standardinformation.model.entity.FermentationDetails;
import com.org.qualitycore.standardinformation.model.entity.FermentationTimedLog;
import com.org.qualitycore.standardinformation.model.entity.MaterialGrinding;
import com.org.qualitycore.standardinformation.model.repository.FermentationDetailsRepository;
import com.org.qualitycore.standardinformation.model.repository.FermentationTimedLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class FermentationTimedLogService {
    private final FermentationTimedLogRepository fermentationTimedLogRepository;
    private final FermentationDetailsRepository fermentationDetailsRepository;
    private final ModelMapper modelMapper;


    // ✅ 발효 공정 ID 목록 조회 서비스
    public List<String> findAllFermentationIds() {
        log.info("서비스: 발효 공정 ID 목록 조회");
        return fermentationDetailsRepository.findFermentationIds();
    }



    // ✅ 발효 시간별 등록
    public FermentationTimedLog saveLog(FermentationTimedLogDTO request) {
        log.info("서비스: 발효 공정 로그 저장 - {}", request);

        // ✅ `fermentationId`를 기반으로 `FermentationDetails` 찾기
        FermentationDetails details = fermentationDetailsRepository.findByFermentationId(request.getFermentationId())
                .orElseThrow(() -> new RuntimeException("발효 공정을 찾을 수 없습니다: " + request.getFermentationId()));

        // ✅ ModelMapper 를 사용하여 DTO 를 엔티티로 변환
        FermentationTimedLog log = modelMapper.map(request, FermentationTimedLog.class);
        log.setFermentationDetails(details);
        log.setStartTime(LocalDateTime.now()); // 시작 시간 설정

        return fermentationTimedLogRepository.save(log);
    }


    // 실제 종료시간 업데이트
    public FermentationTimedLogDTO completeEndTime(Long flogId) {
        FermentationTimedLog fermentationTimedLog = fermentationTimedLogRepository.findById(flogId)
                .orElseThrow(() -> new RuntimeException("발효 시간별 등록  ID가 존재하지 않습니다."));
        fermentationTimedLog.setActualEndTime(LocalDateTime.now());
        FermentationTimedLog updatedFermentationTimedLog = fermentationTimedLogRepository.save(fermentationTimedLog);
        return modelMapper.map(updatedFermentationTimedLog, FermentationTimedLogDTO.class);
    }


}





