package com.org.qualitycore.standardinformation.model.service;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.CarbonationProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.CoolingProcessDTO;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.ProcessTrackingDTONam;
import com.org.qualitycore.standardinformation.model.entity.CarbonationProcess;
import com.org.qualitycore.standardinformation.model.entity.CoolingProcess;
import com.org.qualitycore.standardinformation.model.repository.CarbonationProcessRepository;
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
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CarbonationProcessService {

    private final CarbonationProcessRepository carbonationProcessRepository;
    private final LineMaterialRepository lineMaterialRepository;
    private final ProcessTrackingRepository processTrackingRepository;
    private final ModelMapper modelMapper;


    // ✅ 작업지시 ID 목록 조회
    @Transactional
    public List<LineMaterialNDTO> getLineMaterial() {
        log.info("서비스:탄산조정 작업지시 ID 목록 조회 시작");

        List<LineMaterial> lineMaterialList = lineMaterialRepository.findAllLineMaterial();
        log.info("서비스: 탄산조정 조회된 작업지시 ID 목록 {}", lineMaterialList);
        return lineMaterialList.stream()
                .map(material -> {
                    LineMaterialNDTO dto = modelMapper.map(material, LineMaterialNDTO.class);
                    dto.setLotNo(material.getWorkOrders() != null ? material.getWorkOrders().getLotNo() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }




    // ✅ 탄산 조정 공정 등록
    @Transactional
    public Message createCarbonationProcess(CarbonationProcessDTO carbonationProcessDTO) {
        try {
            log.info("서비스 :탄산 조정 등록 시작 DTO {}", carbonationProcessDTO);

            // DTO 가 null 인지 체크
            if (carbonationProcessDTO == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "carbonationProcessDTO 가 null 임", new HashMap<>());
            }

            // ID 자동 생성
            String generatedId = generateNextCarbonationId();
            log.info("자동으로 생성되는 ID {}", generatedId);


            // ✅ 특정 LOT_NO에 대한 자재 정보 가져오기
            List<LineMaterial> lineMaterials = lineMaterialRepository.
                    findByWorkOrders_LotNo(carbonationProcessDTO.getLotNo());
            if (lineMaterials.isEmpty()) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "LOT_NO가 존재하지 않습니다.", new HashMap<>());
            }

            // ✅ ModelMapper 를 사용하여 DTO -> Entity 변환
            CarbonationProcess carbonationProcess = modelMapper
                    .map(carbonationProcessDTO, CarbonationProcess.class);

            // ✅ ID 자동 생성
            carbonationProcess.setCarbonationId(generatedId);

            // ✅ 관련 엔티티 매핑 (LOT_NO 기반으로 LineMaterial 리스트 설정)
            carbonationProcess.setLineMaterials(lineMaterials);

            // ✅ WorkOrders 가져오기
            WorkOrders workOrders = lineMaterials.get(0).getWorkOrders();

            // LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking = processTrackingRepository
                    .findByLotNo(carbonationProcessDTO.getLotNo());
            if (processTracking == null) {
                processTracking = new processTracking();
            }

            // ✅ `processTracking`에 `WorkOrders` 설정
            processTracking.setWorkOrders(workOrders); // ✅ LOT_NO와 연결

            // ✅ ProcessTracking 에 lotNo를 직접 설정할 수 없으므로, WorkOrders 에서 가져와 사용
            processTracking.setStatusCode("SC009");
            processTracking.setProcessStatus("진행 중");
            processTracking.setProcessName("탄산 조정");

            // ✅ `processTracking`을 `mashingProcess`에 설정
            carbonationProcess.setProcessTracking(processTracking);

            // ✅ 시작 시간 설정 (DTO 값이 있으면 사용, 없으면 현재 시간)
            if (carbonationProcess.getStartTime() == null) {
                carbonationProcess.setStartTime(LocalDateTime.now());
            }

            // ✅ 예상 종료 시간 자동 계산
            if (carbonationProcess.getExpectedEndTime() == null
                    && carbonationProcess.getCarbonationTime() != null) {
                carbonationProcess.setExpectedEndTime(carbonationProcess.getStartTime()
                        .plusMinutes(carbonationProcess.getCarbonationTime()));
            }

            log.info("ModelMapper 변환 완료 !! {}", carbonationProcess);

            // ✅ DB 저장
            CarbonationProcess saveCarbonation = carbonationProcessRepository.save(carbonationProcess);
            log.info("서비스 탄산 조정 공정 등록 완료 ! {}", saveCarbonation);

            // ✅ DTO 변환 후 반환
            CarbonationProcessDTO responseDTO = modelMapper.map(saveCarbonation, CarbonationProcessDTO.class);

            // ✅ lotNo가 누락되지 않도록 직접 설정
            if (saveCarbonation.getProcessTracking() != null
                    && saveCarbonation.getProcessTracking().getWorkOrders() != null) {
                responseDTO.getProcessTracking().setLotNo(
                        saveCarbonation.getProcessTracking().getWorkOrders().getLotNo()
                );
            }

            Map<String, Object> result = new HashMap<>();
            result.put("saveCarbonation", responseDTO);
            return new Message(HttpStatus.CREATED.value(), "탄산 조정공정 등록 완료!", result);


        } catch (IllegalArgumentException e) {
            log.error("서비스 : 입력값 오류 발생 - 이유: {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "입력값 오류: " + e.getMessage(), new HashMap<>());


        } catch (Exception e) {
            log.error("서비스 : 탄산 조정 공정 등록중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "탄산 조정 공정 등록 실패: " + e.getMessage(), new HashMap<>());
        }

    }

    // 가장 큰 "coolingId" 조회 후 다음 ID 생성 하룻 있는 코드!
    public String generateNextCarbonationId() {
        Integer maxId = carbonationProcessRepository.findMaxCarbonationId();
        int nextId = (maxId != null) ? maxId + 1 : 1;
        return String.format("CA%03d", nextId); // "CA001"형식!
    }


    // 실제 종료시간 업데이트
    public CarbonationProcessDTO completeEndTime(String carbonationId) {
        CarbonationProcess carbonationProcess = carbonationProcessRepository.findById(carbonationId)
                .orElseThrow(() -> new RuntimeException("탄산 조정 ID가 존재하지 않습니다."));
        carbonationProcess.setActualEndTime(LocalDateTime.now());
        CarbonationProcess updatedCarbonation = carbonationProcessRepository.save(carbonationProcess);
        return modelMapper.map(updatedCarbonation, CarbonationProcessDTO.class);
    }


    // 공정 상태 코드 추적 ( SC009 , 진행 중 , 탄산 조정공정 업데이트)
    @Transactional
    public Message updateCarbonationProcessStatus(CarbonationProcessDTO carbonationProcessDTO) {
        try {
            log.info("📌 서비스: 업데이트할 processStatus={}",
                    carbonationProcessDTO.getProcessTracking().getProcessStatus());

            // ✅ LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking =
                    processTrackingRepository.findByLotNo(carbonationProcessDTO.getLotNo());

            // ✅ DTO 가 null 인지 체크
            if (carbonationProcessDTO == null || carbonationProcessDTO.getLotNo() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "carbonationProcessDTO 또는 LOT_NO가 null 입니다.", new HashMap<>());
            }

            // ✅ trackingId가 없으면 업데이트 불가
            if (processTracking.getTrackingId() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "ProcessTracking 의 ID가 없습니다.", new HashMap<>());
            }

            // ✅ DTO 에서 ProcessTracking 정보를 가져와서 업데이트
            processTracking.setStatusCode("SC009");
            processTracking.setProcessStatus("진행 중");
            processTracking.setProcessName("탄산 조정");

            log.info("DTO 에서 받은 값: StatusCode={}, ProcessStatus={}, ProcessName={}",
                    processTracking.getStatusCode(), processTracking.getProcessStatus(), processTracking.getProcessName());


            log.info("업데이트된 ProcessTracking: {}", processTracking);

            // ✅ 기존 데이터를 업데이트 (UPDATE 수행)
            processTrackingRepository.save(processTracking);

            // ✅ Hibernate Proxy 를 제거한 DTO 변환 후 반환
            ProcessTrackingDTONam responseDTO = modelMapper.map(processTracking, ProcessTrackingDTONam.class);
            return new Message(HttpStatus.OK.value(),
                    "탄산 조정 공정 상태 업데이트 완료!", Map.of("updatedProcessTracking", responseDTO));

        } catch (Exception e) {
            log.error("서비스 : 탄산 조정 공정 상태 업데이트 중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(),
                    "탄산 조정 공정 상태 업데이트 실패: " + e.getMessage(), new HashMap<>());

        }


    }

}
