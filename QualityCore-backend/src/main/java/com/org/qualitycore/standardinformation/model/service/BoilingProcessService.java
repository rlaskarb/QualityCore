package com.org.qualitycore.standardinformation.model.service;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.BoilingProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.FiltrationProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.ProcessTrackingDTONam;
import com.org.qualitycore.standardinformation.model.entity.BoilingProcess;
import com.org.qualitycore.standardinformation.model.entity.FiltrationProcess;
import com.org.qualitycore.standardinformation.model.repository.BoilingProcessRepository;
import com.org.qualitycore.work.model.entity.LineMaterial;
import com.org.qualitycore.work.model.entity.WorkOrders;
import com.org.qualitycore.work.model.entity.processTracking;
import com.org.qualitycore.work.model.repository.LineMaterialRepository;
import com.org.qualitycore.work.model.repository.ProcessTrackingRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoilingProcessService {

    private final BoilingProcessRepository boilingProcessRepository;
    private final LineMaterialRepository lineMaterialRepository;
    private final ProcessTrackingRepository processTrackingRepository;
    private final ModelMapper modelMapper;


    // ✅ 작업지시 ID 목록 조회
    @Transactional
    public List<LineMaterialNDTO> getLineMaterial() {
        log.info("서비스:끓임 작업지시 ID 목록 조회 시작");

        List<LineMaterial> lineMaterialList = lineMaterialRepository.findAllLineMaterial();
        log.info("서비스: 끓임 조회된 작업지시 ID 목록 {}", lineMaterialList);
        return lineMaterialList.stream()
                .map(material -> {
                    LineMaterialNDTO dto = modelMapper.map(material, LineMaterialNDTO.class);
                    dto.setLotNo(material.getWorkOrders() != null ? material.getWorkOrders().getLotNo() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }


    // ✅ 특정 LOT_NO에 대한 자재 정보 조회
    @Transactional
    public List<LineMaterialNDTO> getMaterialsByLotNo(String lotNo) {
        log.info("서비스:끓임 LOT_NO={}에 대한 자재 정보 조회", lotNo);
        List<LineMaterial> materials = lineMaterialRepository.findByWorkOrders_LotNo(lotNo);

        return materials.stream()
                .map(material -> {
                    LineMaterialNDTO dto = modelMapper.map(material, LineMaterialNDTO.class);
                    dto.setLotNo(material.getWorkOrders() != null ? material.getWorkOrders().getLotNo() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }



    // ✅ 끓임공정 등록
    @Transactional
    public Message createBoilingProcess (BoilingProcessDTO boilingProcessDTO){
        try {
            log.info("서비스 :끓임 등록 시작 DTO {}", boilingProcessDTO);

            // DTO 가 null 인지 체크
            if (boilingProcessDTO == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "boilingProcessDTO 가 null 임", new HashMap<>());
            }

            // ID 자동 생성
            String generatedId = generateNextBoilingId();
            log.info("자동으로 생성되는 ID {}", generatedId);


            // ✅ 특정 LOT_NO에 대한 자재 정보 가져오기
            List<LineMaterial> lineMaterials = lineMaterialRepository.
                    findByWorkOrders_LotNo(boilingProcessDTO.getLotNo());
            if (lineMaterials.isEmpty()) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "LOT_NO가 존재하지 않습니다.", new HashMap<>());
            }

            // ✅ ModelMapper 를 사용하여 DTO -> Entity 변환
            BoilingProcess boilingProcess = modelMapper
                    .map(boilingProcessDTO, BoilingProcess.class);

            // ✅ ID 자동 생성
            boilingProcess.setBoilingId(generatedId);

            // ✅ 관련 엔티티 매핑 (LOT_NO 기반으로 LineMaterial 리스트 설정)
            boilingProcess.setLineMaterials(lineMaterials);

            // ✅ WorkOrders 가져오기
            WorkOrders workOrders = lineMaterials.get(0).getWorkOrders();

            // LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking =processTrackingRepository
                    .findByLotNo(boilingProcessDTO.getLotNo());
            if(processTracking == null){
                processTracking = new processTracking();
            }

            // ✅ `processTracking`에 `WorkOrders` 설정
            processTracking.setWorkOrders(workOrders); // ✅ LOT_NO와 연결

            // ✅ ProcessTracking 에 lotNo를 직접 설정할 수 없으므로, WorkOrders 에서 가져와 사용
            processTracking.setStatusCode("SC004");
            processTracking.setProcessStatus("진행 중");
            processTracking.setProcessName("끓임");

            // ✅ `processTracking`을 `mashingProcess`에 설정
            boilingProcess.setProcessTracking(processTracking);

            // ✅ 시작 시간 설정 (DTO 값이 있으면 사용, 없으면 현재 시간)
            if (boilingProcess.getStartTime() == null) {
                boilingProcess.setStartTime(LocalDateTime.now());
            }

            // ✅ 예상 종료 시간 자동 계산
            if (boilingProcess.getExpectedEndTime() == null
                    && boilingProcess.getBoilingTime() != null) {
                boilingProcess.setExpectedEndTime(boilingProcess.getStartTime()
                        .plusMinutes(boilingProcess.getBoilingTime()));
            }

            log.info("ModelMapper 변환 완료 !! {}", boilingProcess);

            // ✅ DB 저장
            BoilingProcess saveBoilingProcess = boilingProcessRepository.save(boilingProcess);
            log.info("서비스 끓임 공정 등록 완료 ! {}", saveBoilingProcess);

            // ✅ DTO 변환 후 반환
            BoilingProcessDTO responseDTO = modelMapper.map(saveBoilingProcess, BoilingProcessDTO.class);

            // ✅ lotNo가 누락되지 않도록 직접 설정
            if (saveBoilingProcess.getProcessTracking() != null
                    && saveBoilingProcess.getProcessTracking().getWorkOrders() != null) {
                responseDTO.getProcessTracking().setLotNo(
                        saveBoilingProcess.getProcessTracking().getWorkOrders().getLotNo()
                );
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saveBoilingProcess", responseDTO);
            return new Message(HttpStatus.CREATED.value(), "끓임공정 등록 완료!", result);


        }catch(IllegalArgumentException e){
            log.error("서비스 : 입력값 오류 발생 - 이유: {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "입력값 오류: " + e.getMessage(), new HashMap<>());


        } catch(Exception e) {
            log.error("서비스 : 끓임 공정 등록중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "끓임 공정 등록 실패: " + e.getMessage(), new HashMap<>());
        }

    }

    // 가장 큰 "BoilingId" 조회 후 다음 ID 생성 하룻 있는 코드!
    public String generateNextBoilingId(){
        Integer maxId = boilingProcessRepository.findMaxBoilingId();
        int nextId = (maxId != null) ? maxId + 1 : 1;
        return String.format("BO%03d", nextId); // "BO001"형식!
    }


    @Transactional
    public Message updateBoilingProcessByLotNo(String lotNo, Double postBoilWortVolume, Double boilLossVolume) {

        log.info("서비스 : 끓임 공정 업데이트 - LOT_NO {}, 끓임 후 워트량 {}, 끓임 손실량 {}",
                lotNo, postBoilWortVolume, boilLossVolume);

        // ✅ lotNo을 기반으로 끓임 공정 데이터 찾기
        Optional<BoilingProcess> boilingProcessOptional = boilingProcessRepository.findByLotNo(lotNo);
        if (boilingProcessOptional.isEmpty()) {
            log.warn("끓임 공정을 찾을 수 없음 - LOT_NO {}", lotNo);
            return new Message(404, "끓임 공정을 찾을 수 없습니다.", null);
        }

        BoilingProcess boilingProcess = boilingProcessOptional.get();

        if (postBoilWortVolume != null) {
            boilingProcess.setPostBoilWortVolume(postBoilWortVolume);
        }
        if (boilLossVolume != null) {
            boilingProcess.setBoilLossVolume(boilLossVolume);
        }

        boilingProcess.setActualEndTime(LocalDateTime.now());
        BoilingProcess updatedBoilingProcess = boilingProcessRepository.save(boilingProcess);

        Map<String, Object> result = new HashMap<>();
        result.put("updatedBoilingProcess", modelMapper.map(updatedBoilingProcess, BoilingProcessDTO.class));

        log.info("끓임 공정 업데이트 완료 - LOT_NO {}, 결과: {}", lotNo, result);

        return new Message(200, "끓임 공정이 성공적으로 업데이트되었습니다.", result);
    }



    // ✅ LOT_NO로 여러 개의 끓임 공정 조회 (리스트 반환)
    public List<BoilingProcessDTO> getBoilingProcessByLotNo(String lotNo) {
        List<BoilingProcess> boilingProcesses = boilingProcessRepository.findAllByLotNo(lotNo);

        return boilingProcesses.stream()
                .map(bp -> modelMapper.map(bp, BoilingProcessDTO.class))
                .collect(Collectors.toList());
    }

    // 홉이름 및 홉투입량 업데이트 구문
    public Message updateHopInfo(String boilingId, String firstHopName, Double firstHopAmount,
                                 String secondHopName, Double secondHopAmount) {
        Optional<BoilingProcess> boilingProcessOpt = boilingProcessRepository.findById(boilingId);

        if (boilingProcessOpt.isEmpty()) {
            return new Message(404, "해당 ID의 끓임 공정을 찾을 수 없습니다.",null);
        }

        BoilingProcess boilingProcess = boilingProcessOpt.get();

        if (firstHopName != null) boilingProcess.setFirstHopName(firstHopName);
        if (firstHopAmount != null) boilingProcess.setFirstHopAmount(firstHopAmount);
        if (secondHopName != null) boilingProcess.setSecondHopName(secondHopName);
        if (secondHopAmount != null) boilingProcess.setSecondHopAmount(secondHopAmount);

        boilingProcessRepository.save(boilingProcess);

        // 업데이트된 데이터를 응답에 포함
        Map<String, Object> result = new HashMap<>();
        result.put("boilingId", boilingProcess.getBoilingId());
        result.put("firstHopName", boilingProcess.getFirstHopName());
        result.put("firstHopAmount", boilingProcess.getFirstHopAmount());
        result.put("secondHopName", boilingProcess.getSecondHopName());
        result.put("secondHopAmount", boilingProcess.getSecondHopAmount());

        return new Message(200, "홉 투입 정보가 업데이트되었습니다.", result);
    }









    // 공정 상태 코드 추적 ( SC004 , 진행 중 , 끓임공정 업데이트)
    @Transactional
    public Message updateBoilingProcessStatus(BoilingProcessDTO boilingProcessDTO) {
        try {
            log.info("📌 서비스: 업데이트할 processStatus={}",
                    boilingProcessDTO.getProcessTracking().getProcessStatus());

                // ✅ LOT_NO를 기반으로 기존 ProcessTracking 조회
                processTracking processTracking =
                        processTrackingRepository.findByLotNo(boilingProcessDTO.getLotNo());

                // ✅ DTO 가 null 인지 체크
                if (boilingProcessDTO == null || boilingProcessDTO.getLotNo() == null) {
                    return new Message(HttpStatus.BAD_REQUEST.value(),
                            "boilingProcessDTO 또는 LOT_NO가 null 입니다.", new HashMap<>());
                }

                // ✅ trackingId가 없으면 업데이트 불가
                if (processTracking.getTrackingId() == null) {
                    return new Message(HttpStatus.BAD_REQUEST.value(),
                            "ProcessTracking 의 ID가 없습니다.", new HashMap<>());
                }

                // ✅ DTO 에서 ProcessTracking 정보를 가져와서 업데이트
                processTracking.setStatusCode("SC004");
                processTracking.setProcessStatus("진행 중");
                processTracking.setProcessName("끓임");

                log.info("DTO 에서 받은 값: StatusCode={}, ProcessStatus={}, ProcessName={}",
                        processTracking.getStatusCode(), processTracking.getProcessStatus(), processTracking.getProcessName());


                log.info("업데이트된 ProcessTracking: {}", processTracking);

                // ✅ 기존 데이터를 업데이트 (UPDATE 수행)
                processTrackingRepository.save(processTracking);

                // ✅ Hibernate Proxy 를 제거한 DTO 변환 후 반환
                ProcessTrackingDTONam responseDTO = modelMapper.map(processTracking, ProcessTrackingDTONam.class);
                return new Message(HttpStatus.OK.value(),
                        "끓임 공정 상태 업데이트 완료!", Map.of("updatedProcessTracking", responseDTO));

            } catch (Exception e) {
                log.error("서비스 : 끓임 공정 상태 업데이트 중 오류 발생 {}", e.getMessage(), e);
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "끓임 공정 상태 업데이트 실패: " + e.getMessage(), new HashMap<>());

            }

    }






}
