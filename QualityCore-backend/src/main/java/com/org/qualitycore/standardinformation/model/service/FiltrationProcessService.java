package com.org.qualitycore.standardinformation.model.service;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.FiltrationProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.ProcessTrackingDTONam;
import com.org.qualitycore.standardinformation.model.entity.FiltrationProcess;
import com.org.qualitycore.standardinformation.model.entity.MashingProcess;
import com.org.qualitycore.standardinformation.model.repository.FiltrationProcessRepository;
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
public class FiltrationProcessService {

    private final FiltrationProcessRepository filtrationProcessRepository;
    private final LineMaterialRepository lineMaterialRepository;
    private final ProcessTrackingRepository processTrackingRepository;
    private final ModelMapper modelMapper;


    // 여과 공정 전체 조회
    public List<FiltrationProcessDTO> getAllFiltrationProcesses() {
        List<FiltrationProcess> filtrationProcesses = filtrationProcessRepository.findAll();
        return filtrationProcesses.stream()
                .map(process -> modelMapper.map(process, FiltrationProcessDTO.class))
                .collect(Collectors.toList());
    }


    // 여과 공정 상세 조회
    public List<FiltrationProcessDTO> getFiltrationProcessesByLotNo(String lotNo) {
        List<FiltrationProcess> filtrationProcesses = filtrationProcessRepository.findAllByLotNo(lotNo);
        return filtrationProcesses.stream()
                .map(process -> modelMapper.map(process, FiltrationProcessDTO.class))
                .collect(Collectors.toList());
    }



    // ✅ 작업지시 ID 목록 조회
    @Transactional
    public List<LineMaterialNDTO> getLineMaterial() {
        log.info("서비스:여과 작업지시 ID 목록 조회 시작");

        List<LineMaterial> lineMaterialList = lineMaterialRepository.findAllLineMaterial();
        log.info("서비스: 여과 조회된 작업지시 ID 목록 {}", lineMaterialList);
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
        log.info("서비스:여과 LOT_NO={}에 대한 자재 정보 조회", lotNo);
        List<LineMaterial> materials = lineMaterialRepository.findByWorkOrders_LotNo(lotNo);

        return materials.stream()
                .map(material -> {
                    LineMaterialNDTO dto = modelMapper.map(material, LineMaterialNDTO.class);
                    dto.setLotNo(material.getWorkOrders() != null ? material.getWorkOrders().getLotNo() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }



    // ✅ 여과공정 등록
    @Transactional
    public Message createFiltrationProcess (FiltrationProcessDTO filtrationProcessDTO){
        try {
            log.info("서비스 :여과 등록 시작 DTO {}", filtrationProcessDTO);

            // DTO 가 null 인지 체크
            if (filtrationProcessDTO == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "filtrationProcessDTO 가 null 임", new HashMap<>());
            }

            // ID 자동 생성
            String generatedId = generateNextFiltrationId();
            log.info("자동으로 생성되는 ID {}", generatedId);


            // ✅ 특정 LOT_NO에 대한 자재 정보 가져오기
            List<LineMaterial> lineMaterials = lineMaterialRepository.
                    findByWorkOrders_LotNo(filtrationProcessDTO.getLotNo());
            if (lineMaterials.isEmpty()) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "LOT_NO가 존재하지 않습니다.", new HashMap<>());
            }

            // ✅ ModelMapper 를 사용하여 DTO -> Entity 변환
            FiltrationProcess filtrationProcess = modelMapper
                    .map(filtrationProcessDTO, FiltrationProcess.class);

            // ✅ ID 자동 생성
            filtrationProcess.setFiltrationId(generatedId);

            // ✅ 관련 엔티티 매핑 (LOT_NO 기반으로 LineMaterial 리스트 설정)
            filtrationProcess.setLineMaterials(lineMaterials);

            // ✅ WorkOrders 가져오기
            WorkOrders workOrders = lineMaterials.get(0).getWorkOrders();

            // LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking =processTrackingRepository
                    .findByLotNo(filtrationProcessDTO.getLotNo());
            if(processTracking == null){
                processTracking = new processTracking();
            }

            // ✅ `processTracking`에 `WorkOrders` 설정
            processTracking.setWorkOrders(workOrders); // ✅ LOT_NO와 연결

            // ✅ ProcessTracking 에 lotNo를 직접 설정할 수 없으므로, WorkOrders 에서 가져와 사용
            processTracking.setStatusCode("SC003");
            processTracking.setProcessStatus("진행 중");
            processTracking.setProcessName("여과");

            // ✅ `processTracking`을 `mashingProcess`에 설정
            filtrationProcess.setProcessTracking(processTracking);

            // ✅ 시작 시간 설정 (DTO 값이 있으면 사용, 없으면 현재 시간)
            if (filtrationProcess.getStartTime() == null) {
                filtrationProcess.setStartTime(LocalDateTime.now());
            }

            // ✅ 예상 종료 시간 자동 계산
            if (filtrationProcess.getExpectedEndTime() == null
                    && filtrationProcess.getFiltrationTime() != null) {
                filtrationProcess.setExpectedEndTime(filtrationProcess.getStartTime()
                        .plusMinutes(filtrationProcess.getFiltrationTime()));
            }

            log.info("ModelMapper 변환 완료 !! {}", filtrationProcess);

            // ✅ DB 저장
            FiltrationProcess saveFiltrationProcess = filtrationProcessRepository.save(filtrationProcess);
            log.info("서비스 여과 공정 등록 완료 ! {}", saveFiltrationProcess);

            // ✅ DTO 변환 후 반환
            FiltrationProcessDTO responseDTO = modelMapper.map(saveFiltrationProcess, FiltrationProcessDTO.class);

            // ✅ lotNo가 누락되지 않도록 직접 설정
            if (saveFiltrationProcess.getProcessTracking() != null
                    && saveFiltrationProcess.getProcessTracking().getWorkOrders() != null) {
                responseDTO.getProcessTracking().setLotNo(
                        saveFiltrationProcess.getProcessTracking().getWorkOrders().getLotNo()
                );
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saveFiltrationProcess", responseDTO);
            return new Message(HttpStatus.CREATED.value(), "여과공정 등록 완료!", result);


        }catch(IllegalArgumentException e){
            log.error("서비스 : 입력값 오류 발생 - 이유: {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "입력값 오류: " + e.getMessage(), new HashMap<>());


        } catch(Exception e) {
            log.error("서비스 : 여과공정 등록중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "여과 공정 등록 실패: " + e.getMessage(), new HashMap<>());
        }

    }

    // 가장 큰 "filtrationId" 조회 후 다음 ID 생성 하룻 있는 코드!
    public String generateNextFiltrationId(){
        Integer maxId = filtrationProcessRepository.findMaxFiltrationId();
        int nextId = (maxId != null) ? maxId + 1 : 1;
        return String.format("FI%03d", nextId); // "FI001"형식!
    }


    // 여과 공정 업데이트  회수된 워트량 , 손실량 , 실제 종료시간
    @Transactional
    public Message updateFiltrationProcess
            (String filtrationId, Double recoveredWortVolume, Double lossVolume) {

        log.info("서비스 : 여과 공정 업데이트 - ID {}, 회수된 워트량 {}, 손실량 {}, 실제 종료시간 {}",
                filtrationId, recoveredWortVolume, lossVolume);


        Optional<FiltrationProcess> filtrationProcessOptional = filtrationProcessRepository.findById(filtrationId);
        if (filtrationProcessOptional.isEmpty()) {
            log.warn("여과 공정을 찾을 수 없음 - ID {}", filtrationId);
            return new Message(404, "여과 공정을 찾을 수 없습니다.", null);
        }

        FiltrationProcess filtrationProcess = filtrationProcessOptional.get();

        if (recoveredWortVolume != null) {
            filtrationProcess.setRecoveredWortVolume(recoveredWortVolume);
        }
        if (lossVolume != null) {
            filtrationProcess.setLossVolume(lossVolume);
        }


        filtrationProcess.setActualEndTime(LocalDateTime.now());
        FiltrationProcess updatedFiltration = filtrationProcessRepository.save(filtrationProcess);


        Map<String, Object> result = new HashMap<>();
        result.put("updatedFiltration", modelMapper.map(updatedFiltration,FiltrationProcessDTO.class));


        log.info("여과 공정 업데이트 완료 - ID {}, 결과: {}", filtrationId, result);

        return new Message(200, "여과 공정이 성공적으로 업데이트되었습니다.", result);
    }


    // 공정 상태 코드 추적 ( SC003 , 진행 중 , 여과공정 업데이트)
    @Transactional
    public Message updateFiltrationProcessStatus(FiltrationProcessDTO filtrationProcessDTO) {
        try {
            log.info("📌 서비스: 업데이트할 processStatus={}",
                    filtrationProcessDTO.getProcessTracking().getProcessStatus());

            // ✅ LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking =
                    processTrackingRepository.findByLotNo(filtrationProcessDTO.getLotNo());

            // ✅ DTO 가 null 인지 체크
            if (filtrationProcessDTO == null || filtrationProcessDTO.getLotNo() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "filtrationProcessDTO 또는 LOT_NO가 null 입니다.", new HashMap<>());
            }

            // ✅ trackingId가 없으면 업데이트 불가
            if (processTracking.getTrackingId() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "ProcessTracking 의 ID가 없습니다.", new HashMap<>());
            }

            // ✅ DTO 에서 ProcessTracking 정보를 가져와서 업데이트

                processTracking.setStatusCode("SC003"); // ✅ 상태 코드 설정
                processTracking.setProcessStatus("진행 중"); // ✅ 공정 상태 설정
                processTracking.setProcessName("여과"); // ✅ 공정 이름 설정

                log.info("DTO 에서 받은 값: StatusCode={}, ProcessStatus={}, ProcessName={}",
                        processTracking.getStatusCode(), processTracking.getProcessStatus(), processTracking.getProcessName());


            log.info("업데이트된 ProcessTracking: {}", processTracking);

            // ✅ 기존 데이터를 업데이트 (UPDATE 수행)
            processTrackingRepository.save(processTracking);

            // ✅ Hibernate Proxy 를 제거한 DTO 변환 후 반환
            ProcessTrackingDTONam responseDTO = modelMapper.map(processTracking, ProcessTrackingDTONam.class);
            return new Message(HttpStatus.OK.value(),
                    "여과 공정 상태 업데이트 완료!", Map.of("updatedProcessTracking", responseDTO));

        } catch (Exception e) {
            log.error("서비스 : 여과 공정 상태 업데이트 중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(),
                    "여과 공정 상태 업데이트 실패: " + e.getMessage(), new HashMap<>());
        }
    }

}


