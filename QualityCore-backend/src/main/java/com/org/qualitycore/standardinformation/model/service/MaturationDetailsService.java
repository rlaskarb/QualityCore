package com.org.qualitycore.standardinformation.model.service;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.*;
import com.org.qualitycore.standardinformation.model.entity.*;
import com.org.qualitycore.standardinformation.model.repository.MaturationDetailsRepository;
import com.org.qualitycore.standardinformation.model.repository.MaturationTimedLogRepository;
import com.org.qualitycore.work.model.entity.LineMaterial;
import com.org.qualitycore.work.model.entity.WorkOrders;
import com.org.qualitycore.work.model.entity.processTracking;
import com.org.qualitycore.work.model.repository.LineMaterialRepository;
import com.org.qualitycore.work.model.repository.ProcessTrackingRepository;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class MaturationDetailsService {

    private final MaturationDetailsRepository maturationDetailsRepository;
    private final MaturationTimedLogRepository maturationTimedLogRepository;
    private final LineMaterialRepository lineMaterialRepository;
    private final ProcessTrackingRepository processTrackingRepository;
    private final ModelMapper modelMapper;
    private final JPAQueryFactory queryFactory;


    // ✅ 작업지시 ID 목록 조회
    @Transactional
    public List<LineMaterialNDTO> getLineMaterial() {
        log.info("서비스:숙성 상세 공정 작업지시 ID 목록 조회 시작");

        List<LineMaterial> lineMaterialList = lineMaterialRepository.findAllLineMaterial();
        log.info("서비스: 숙성 상세 공정 조회된 작업지시 ID 목록 {}", lineMaterialList);
        return lineMaterialList.stream()
                .map(material -> {
                    LineMaterialNDTO dto = modelMapper.map(material, LineMaterialNDTO.class);
                    dto.setLotNo(material.getWorkOrders() != null ? material.getWorkOrders().getLotNo() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ✅ 숙성 상세 공정 등록
    @Transactional
    public Message createMaturationDetails(MaturationDetailsDTO maturationDetailsDTO) {
        try {
            log.info("서비스 :숙성 상세 공정 등록 시작 DTO {}", maturationDetailsDTO);

            // DTO 가 null 인지 체크
            if (maturationDetailsDTO == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "maturationDetailsDTO 가 null 임", new HashMap<>());
            }

            // ID 자동 생성
            String generatedId = generateNextMaturationId();
            log.info("자동으로 생성되는 ID {}", generatedId);


            // ✅ 특정 LOT_NO에 대한 자재 정보 가져오기
            List<LineMaterial> lineMaterials = lineMaterialRepository.
                    findByWorkOrders_LotNo(maturationDetailsDTO.getLotNo());
            if (lineMaterials.isEmpty()) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "LOT_NO가 존재하지 않습니다.", new HashMap<>());
            }

            // ✅ ModelMapper 를 사용하여 DTO -> Entity 변환
            MaturationDetails maturationDetails = modelMapper
                    .map(maturationDetailsDTO, MaturationDetails.class);

            // ✅ ID 자동 생성
            maturationDetails.setMaturationId(generatedId);

            // ✅ 관련 엔티티 매핑 (LOT_NO 기반으로 LineMaterial 리스트 설정)
            maturationDetails.setLineMaterials(lineMaterials);

            // ✅ WorkOrders 가져오기
            WorkOrders workOrders = lineMaterials.get(0).getWorkOrders();

            // LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking = processTrackingRepository
                    .findByLotNo(maturationDetailsDTO.getLotNo());
            if (processTracking == null) {
                processTracking = new processTracking();
            }

            // ✅ `processTracking`에 `WorkOrders` 설정
            processTracking.setWorkOrders(workOrders); // ✅ LOT_NO와 연결

            // ✅ ProcessTracking 에 lotNo를 직접 설정할 수 없으므로, WorkOrders 에서 가져와 사용
            processTracking.setStatusCode("SC007");
            processTracking.setProcessStatus("진행 중");
            processTracking.setProcessName("숙성");

            // ✅ `processTracking`을 `mashingProcess`에 설정
            maturationDetails.setProcessTracking(processTracking);

            // ✅ 시작 시간 설정 (DTO 값이 있으면 사용, 없으면 현재 시간)
            if (maturationDetails.getStartTime() == null) {
                maturationDetails.setStartTime(LocalDateTime.now());
            }

            log.info("ModelMapper 변환 완료 !! {}", maturationDetails);

            // ✅ DB 저장
            MaturationDetails saveMaturationDetails = maturationDetailsRepository.save(maturationDetails);
            log.info("서비스 숙성 상세 공정 등록 완료 ! {}", saveMaturationDetails);

            // ✅ DTO 변환 후 반환
            MaturationDetailsDTO responseDTO = modelMapper.map(saveMaturationDetails, MaturationDetailsDTO.class);

            // ✅ lotNo가 누락되지 않도록 직접 설정
            if (saveMaturationDetails.getProcessTracking() != null
                    && saveMaturationDetails.getProcessTracking().getWorkOrders() != null) {
                responseDTO.getProcessTracking().setLotNo(
                        saveMaturationDetails.getProcessTracking().getWorkOrders().getLotNo()
                );
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saveMaturationDetails", responseDTO);
            return new Message(HttpStatus.CREATED.value(), "숙성 상세 공정 등록 완료!", result);


        } catch (IllegalArgumentException e) {
            log.error("서비스 : 입력값 오류 발생 - 이유: {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "입력값 오류: " + e.getMessage(), new HashMap<>());


        } catch (Exception e) {
            log.error("서비스 : 숙성 상세 공정 등록중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "숙성 상세 공정 등록 실패: " + e.getMessage(), new HashMap<>());
        }

    }

    // 가장 큰 "MaturationId" 조회 후 다음 ID 생성 하룻 있는 코드!
    public String generateNextMaturationId() {
        Integer maxId = maturationDetailsRepository.findMaxMaturationId();
        int nextId = (maxId != null) ? maxId + 1 : 1;
        return String.format("MAR%03d", nextId); // "MAR001"형식!
    }



    // 실제 종료시간 업데이트
    public MaturationDetailsDTO completeEndTime(String maturationId) {
        MaturationDetails maturationDetails = maturationDetailsRepository.findById(maturationId)
                .orElseThrow(() -> new RuntimeException("숙성 시간별 등록  ID가 존재하지 않습니다."));
        maturationDetails.setEndTime(LocalDateTime.now());
        MaturationDetails updatedMaturationDetails = maturationDetailsRepository.save(maturationDetails);
        return modelMapper.map(updatedMaturationDetails, MaturationDetailsDTO.class);
    }

    // 공정 상태 코드 추적 ( SC007 , 진행 중 , 숙성 상세 공정 업데이트)
    @Transactional
    public Message updateMaturationDetailsStatus (MaturationDetailsDTO maturationDetailsDTO) {
        try {
            log.info("📌 서비스: 업데이트할 processStatus={}",
                    maturationDetailsDTO.getProcessTracking().getProcessStatus());

            // ✅ LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking =
                    processTrackingRepository.findByLotNo(maturationDetailsDTO.getLotNo());

            // ✅ DTO 가 null 인지 체크
            if (maturationDetailsDTO == null || maturationDetailsDTO.getLotNo() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "maturationDetailsDTO 또는 LOT_NO가 null 입니다.", new HashMap<>());
            }

            // ✅ trackingId가 없으면 업데이트 불가
            if (processTracking.getTrackingId() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "ProcessTracking 의 ID가 없습니다.", new HashMap<>());
            }

            // ✅ DTO 에서 ProcessTracking 정보를 가져와서 업데이트
            processTracking.setStatusCode("SC007");
            processTracking.setProcessStatus("진행 중");
            processTracking.setProcessName("숙성 상세");

            log.info("DTO 에서 받은 값: StatusCode={}, ProcessStatus={}, ProcessName={}",
                    processTracking.getStatusCode(), processTracking.getProcessStatus(), processTracking.getProcessName());


            log.info("업데이트된 ProcessTracking: {}", processTracking);

            // ✅ 기존 데이터를 업데이트 (UPDATE 수행)
            processTrackingRepository.save(processTracking);

            // ✅ Hibernate Proxy 를 제거한 DTO 변환 후 반환
            ProcessTrackingDTONam responseDTO = modelMapper.map(processTracking, ProcessTrackingDTONam.class);
            return new Message(HttpStatus.OK.value(),
                    "숙성 상세 공정 상태 업데이트 완료!", Map.of("updatedProcessTracking", responseDTO));

        } catch (Exception e) {
            log.error("서비스 : 숙성 상세 공정 상태 업데이트 중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(),
                    "숙성 상세 공정 상태 업데이트 실패: " + e.getMessage(), new HashMap<>());
        }

    }


    // 숙성 시간별 전체 조회

    // ✅ `maturationId` 없이 전체 조회
    public List<MaturationTimedLogDTO> getAllTimedLogsWithoutId() {
        log.info("전체 숙성 시간대별 데이터 조회");
        List<MaturationTimedLog> logs = maturationTimedLogRepository.findAll();
        return logs.stream()
                .map(log -> modelMapper.map(log, MaturationTimedLogDTO.class))
                .collect(Collectors.toList());
    }

    // ✅ `maturationId` 기준으로 조회
    public List<MaturationTimedLogDTO> getAllTimedLogsById(String maturationId) {
        log.info("특정 발효 ID({})의 시간대별 데이터 조회", maturationId);
        List<MaturationTimedLog> logs = maturationTimedLogRepository.findAllByMaturationDetails_MaturationId(maturationId);
        return logs.stream()
                .map(log -> modelMapper.map(log, MaturationTimedLogDTO.class))
                .collect(Collectors.toList());
    }

    // 숙성 전체조회
    public List<MaturationDetailsDTO> findAllMaturation() {
        QMaturationDetails maturation = QMaturationDetails.maturationDetails;

        return queryFactory
                .select(Projections.fields(MaturationDetailsDTO.class,
                        maturation.maturationId.as("maturationId"),
                        maturation.lotNo.as("lotNo"),
                        maturation.maturationTime.as("maturationTime"),
                        maturation.startTemperature.as("startTemperature"),
                        maturation.startTime.as("startTime"),
                        maturation.endTime.as("endTime"),
                        maturation.notes.as("notes"),
                        maturation.temperature.as("temperature"),
                        maturation.pressure.as("pressure"),
                        maturation.co2Percent.as("co2Percent"),
                        maturation.dissolvedOxygen.as("dissolvedOxygen")
                ))
                .from(maturation)
                .fetch();
    }

    // 숙성 상세조회
    public MaturationDetailsDTO findByMaturationId(String maturationId) {

            QMaturationDetails maturation = QMaturationDetails.maturationDetails;

            return queryFactory
                    .select(Projections.fields(MaturationDetailsDTO.class,
                            maturation.maturationId.as("maturationId"),
                            maturation.lotNo.as("lotNo"),
                            maturation.maturationTime.as("maturationTime"),
                            maturation.startTemperature.as("startTemperature"),
                            maturation.startTime.as("startTime"),
                            maturation.endTime.as("endTime"),
                            maturation.notes.as("notes"),
                            maturation.temperature.as("temperature"),
                            maturation.pressure.as("pressure"),
                            maturation.co2Percent.as("co2Percent"),
                            maturation.dissolvedOxygen.as("dissolvedOxygen")
                    ))
                    .from(maturation)
                    .where(maturation.maturationId.eq(maturationId))
                    .fetchOne();
        }

    }

