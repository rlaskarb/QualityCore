package com.org.qualitycore.standardinformation.model.service;



import com.org.qualitycore.standardinformation.model.dto.MaturationTimedLogDTO;
import com.org.qualitycore.standardinformation.model.entity.*;
import com.org.qualitycore.standardinformation.model.repository.MaturationDetailsRepository;
import com.org.qualitycore.standardinformation.model.repository.MaturationTimedLogRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class MaturationTimedLogService {
    private final MaturationTimedLogRepository maturationTimedLogRepository;
    private final MaturationDetailsRepository maturationDetailsRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;


    // ✅ 숙성 공정 ID 목록 조회 서비스
    public List<String> findAllMaturationIds() {
        log.info("서비스: 숙성 공정 ID 목록 조회");
        return maturationDetailsRepository.findMaturationIds();
    }

    // ✅ 숙성 시간별 등록 + 평균값 자동 계산
    public MaturationTimedLog logMaturationData(MaturationTimedLogDTO maturationTimedLogDTO) {
        log.info("서비스: 숙성 공정 로그 저장 - {}", maturationTimedLogDTO);

        // 1. 숙성 공정 조회
        MaturationDetails details = maturationDetailsRepository.findByMaturationId(maturationTimedLogDTO.getMaturationId())
                .orElseThrow(() -> new RuntimeException("숙성 공정을 찾을 수 없습니다: " + maturationTimedLogDTO.getMaturationId()));

        // 2. 로그 저장
        MaturationTimedLog log = modelMapper.map(maturationTimedLogDTO, MaturationTimedLog.class);
        log.setMaturationDetails(details);
        log.setStartTime(LocalDateTime.now());
        MaturationTimedLog savedLog = maturationTimedLogRepository.save(log);

        // 3. 평균값 계산 및 업데이트
        updateAverages(details.getMaturationId());

        return savedLog;
    }

    // 평균값 계산 메서드
    private void updateAverages(String maturationId) {
        // 3-1. 해당 공정의 모든 로그 조회
        // 수정 코드
        List<MaturationTimedLog> logs = maturationTimedLogRepository.findByMaturationId(maturationId);

        // 3-2. 평균 계산
        double avgTemp = logs.stream().mapToDouble(MaturationTimedLog::getTemperature).average().orElse(0);
        double avgPressure = logs.stream().mapToDouble(MaturationTimedLog::getPressure).average().orElse(0);
        double avgCo2 = logs.stream().mapToDouble(MaturationTimedLog::getCo2Percent).average().orElse(0);
        double avgOxygen = logs.stream().mapToDouble(MaturationTimedLog::getDissolvedOxygen).average().orElse(0);

        // 3-3. MATURATION_DETAILS 업데이트
        MaturationDetails details = maturationDetailsRepository.findByMaturationId(maturationId)
                .orElseThrow(() -> new RuntimeException("숙성 공정 없음"));
        details.setTemperature(avgTemp);
        details.setPressure(avgPressure);
        details.setCo2Percent(avgCo2);
        details.setDissolvedOxygen(avgOxygen);
        maturationDetailsRepository.save(details);
    }



    // 실제 종료시간 업데이트
    public MaturationTimedLogDTO completeEndTime(Long flogId) {
        MaturationTimedLog maturationTimedLog = maturationTimedLogRepository.findById(flogId)
                .orElseThrow(() -> new RuntimeException("숙성 시간별 등록  ID가 존재하지 않습니다."));
        maturationTimedLog.setActualEndTime(LocalDateTime.now());
        MaturationTimedLog updatedMaturationTimedLog = maturationTimedLogRepository.save(maturationTimedLog);
        return modelMapper.map(updatedMaturationTimedLog, MaturationTimedLogDTO.class);
    }


}
