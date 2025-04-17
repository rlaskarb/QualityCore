package com.org.qualitycore.standardinformation.model.service;

import com.org.qualitycore.common.Message;
import com.org.qualitycore.work.model.entity.WorkOrders;
import com.org.qualitycore.work.model.entity.processTracking;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.MaterialGrindingDTO;
import com.org.qualitycore.standardinformation.model.dto.ProcessTrackingDTONam;
import com.org.qualitycore.work.model.entity.LineMaterial;
import com.org.qualitycore.standardinformation.model.entity.MaterialGrinding;
import com.org.qualitycore.work.model.repository.LineMaterialRepository;
import com.org.qualitycore.standardinformation.model.repository.MaterialGrindingRepository;
import com.org.qualitycore.work.model.repository.ProcessTrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialGrindingService {

        private final MaterialGrindingRepository materialGrindingRepository;
        private final LineMaterialRepository lineMaterialRepository;
        private final ProcessTrackingRepository processTrackingRepository;
        private final ModelMapper modelMapper;


    // ✅ 작업지시 ID 목록 조회
    @Transactional
    public List<LineMaterialNDTO> getLineMaterial() {
        log.info("서비스: 작업지시 ID 목록 조회 시작");
        List<LineMaterial> lineMaterialList = lineMaterialRepository.findAllLineMaterial();
        log.info("서비스: 조회된 작업지시 ID 목록 {}", lineMaterialList);
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
        log.info("서비스: LOT_NO={}에 대한 자재 정보 조회", lotNo);
        List<LineMaterial> materials = lineMaterialRepository.findByWorkOrders_LotNo(lotNo);
        return materials.stream()
                .map(material -> {
                    LineMaterialNDTO dto = modelMapper.map(material, LineMaterialNDTO.class);
                    dto.setLotNo(material.getWorkOrders() != null ? material.getWorkOrders().getLotNo() : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // ✅ 분쇄 공정 등록 작업지시ID 조회
    public Message getMaterialGrindingByLotNo(String lotNo) {
        List<MaterialGrinding> grindingData = materialGrindingRepository.findByLotNo(lotNo);

        if (grindingData.isEmpty()) {
            return new Message(404, "등록된 분쇄공정 데이터 없음", Collections.emptyMap());
        }

        // ✅ ModelMapper 를 사용하지 않고 DTO 변환 (직렬화 문제 방지)
        List<MaterialGrindingDTO> grindingDTOs = grindingData.stream()
                .map(MaterialGrindingDTO::fromEntity) // DTO 변환 메서드 사용
                .collect(Collectors.toList());


        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("data", grindingDTOs);

        return new Message(200, "조회 성공", responseMap);
    }



    // ✅ 분쇄 공정 등록 전체 조회
    public Message getAllMaterialGrinding() {
        List<MaterialGrinding> grindingData = materialGrindingRepository.findAll();

        if (grindingData.isEmpty()) {
            return new Message(404, "등록된 분쇄공정 데이터 없음", Collections.emptyMap());
        }

        // DTO 변환 후 Map으로 반환
        List<MaterialGrindingDTO> grindingDTOs = grindingData.stream()
                .map(MaterialGrindingDTO::fromEntity)
                .collect(Collectors.toList());

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("data", grindingDTOs);

        return new Message(200, "조회 성공", responseMap);
    }






    // ✅ 분쇄 공정 등록
        @Transactional
        public Message createMaterialGrinding(MaterialGrindingDTO materialGrindingDTO) {

            try {
                log.info("서비스 : 분쇄공정 등록 시작 DTO {}", materialGrindingDTO);

                // ✅ DTO 가 null 인지 체크
                if (materialGrindingDTO == null) {
                    return new Message(HttpStatus.BAD_REQUEST.value(),
                            "MaterialGrindingDTO 가 null 입니다.", new HashMap<>());
                }

                // ID 자동 생성
                String generatedId = generateNextGrindingId();
                log.info("자동으로 생성되는 ID {}", generatedId);


                // ✅ 특정 LOT_NO에 대한 자재 정보 가져오기
                List<LineMaterial> lineMaterials = lineMaterialRepository
                        .findByWorkOrders_LotNo(materialGrindingDTO.getLotNo());
                if (lineMaterials.isEmpty()) {
                    return new Message(HttpStatus.BAD_REQUEST.value()
                            , "LOT_NO가 존재하지 않습니다.", null);
                }

                // ✅ ModelMapper 를 사용하여 DTO -> Entity 변환
                MaterialGrinding materialGrinding = modelMapper
                        .map(materialGrindingDTO, MaterialGrinding.class);

                // ✅ ID 자동 생성 적용
                materialGrinding.setGrindingId(generatedId);

                // ✅ 관련 엔티티 매핑 (LOT_NO 기반으로 LineMaterial 리스트 설정)
                materialGrinding.setLineMaterials(lineMaterials);


                // ✅ WorkOrders 가져오기
                WorkOrders workOrders = lineMaterials.get(0).getWorkOrders();

                // ✅ LOT_NO를 기반으로 기존 ProcessTracking 조회
                processTracking processTracking = processTrackingRepository.
                        findByLotNo(materialGrindingDTO.getLotNo());
                if (processTracking == null) {
                    processTracking = new processTracking();
                }


                // ✅ `processTracking`에 `WorkOrders` 설정
                processTracking.setWorkOrders(workOrders);  // ✅ LOT_NO와 연결

                // ✅ ProcessTracking 에 lotNo를 직접 설정할 수 없으므로, WorkOrders 에서 가져와 사용
                processTracking.setStatusCode("SC001");
                processTracking.setProcessStatus("진행 중");
                processTracking.setProcessName("분쇄");


                // ✅ ProcessTracking 저장
                processTracking = processTrackingRepository.save(processTracking);

                // ✅ `processTracking`을 `materialGrinding`에 설정
                materialGrinding.setProcessTracking(processTracking);


                // ✅ 시작 시간 설정 (DTO 값이 있으면 사용, 없으면 현재 시간)
                if (materialGrinding.getStartTime() == null) {
                    materialGrinding.setStartTime(LocalDateTime.now());
                }

                // ✅ 예상 종료 시간 자동 계산
                if (materialGrinding.getExpectedEndTime() == null && materialGrinding.getGrindDuration() != null) {
                    materialGrinding.setExpectedEndTime(materialGrinding.getStartTime().plusMinutes(materialGrinding.getGrindDuration()));
                }



                log.info("ModelMapper 변환 완료 !! {}", materialGrinding);

                // ✅  DB 저장
                MaterialGrinding savedMaterialGrinding = materialGrindingRepository.save(materialGrinding);
                log.info("서비스 분쇄 공정 등록 완료 ! {}", savedMaterialGrinding);


                // ✅ DTO 변환 후 반환
                MaterialGrindingDTO responseDTO = modelMapper.map(savedMaterialGrinding, MaterialGrindingDTO.class);

                // ✅ lotNo가 누락되지 않도록 직접 설정
                if (savedMaterialGrinding.getProcessTracking() != null
                        && savedMaterialGrinding.getProcessTracking().getWorkOrders() != null) {
                    responseDTO.getProcessTracking().setLotNo(
                            savedMaterialGrinding.getProcessTracking().getWorkOrders().getLotNo()
                    );
                }

                Map<String, Object> result = new HashMap<>();
                result.put("savedMaterialGrinding", responseDTO);
                return new Message(HttpStatus.CREATED.value(), "분쇄공정 등록 완료!", result);


            } catch(IllegalArgumentException e){
                log.error("서비스 : 입력값 오류 발생 - 이유: {}", e.getMessage(), e);
                return new Message(HttpStatus.BAD_REQUEST.value(), "입력값 오류: " + e.getMessage(),new HashMap<>());

            } catch(Exception e) {
                log.error("서비스 : 분쇄공정 등록중 오류 발생 {}", e.getMessage(), e);
                return new Message(HttpStatus.BAD_REQUEST.value(), "분쇄 공정 등록 실패" + e.getMessage(),new HashMap<>());
            }
        }


        // 가장 큰 "grindingId" 조회 후 다음 ID 생성 하룻 있는 코드!
        public String generateNextGrindingId(){
            Integer maxId = materialGrindingRepository.findMaxGrindingId();
            int nextId = (maxId != null) ? maxId + 1 : 1;
            return String.format("GR%03d", nextId); // "GR001"형식!

        }


        // 실제 종료시간 업데이트
        public MaterialGrindingDTO completeEndTime(String grindingId) {
            MaterialGrinding materialGrinding = materialGrindingRepository.findById(grindingId)
                .orElseThrow(() -> new RuntimeException("분쇄 ID가 존재하지 않습니다."));
            materialGrinding.setActualEndTime(LocalDateTime.now());
            MaterialGrinding updatedGrinding = materialGrindingRepository.save(materialGrinding);
            return modelMapper.map(updatedGrinding, MaterialGrindingDTO.class);
        }



    @Transactional
    public Message updateMaterialGrinding(MaterialGrindingDTO materialGrindingDTO) {
        try {
            log.info("서비스 : 분쇄공정 업데이트 시작 DTO {}", materialGrindingDTO);

            // ✅ LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking =
                    processTrackingRepository.findByLotNo(materialGrindingDTO.getLotNo());


            // ✅ DTO 가 null 인지 체크
            if (materialGrindingDTO == null || materialGrindingDTO.getLotNo() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "MaterialGrindingDTO 또는 LOT_NO가 null 입니다.", new HashMap<>());
            }


            // ✅ trackingId가 없으면 업데이트 불가
            if (processTracking.getTrackingId() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "ProcessTracking 의 ID가 없습니다.", new HashMap<>());
            }


            // ✅ DTO 에서 ProcessTracking 정보를 가져와서 업데이트
            if (materialGrindingDTO.getProcessTracking() != null) {
                ProcessTrackingDTONam trackingDTO = materialGrindingDTO.getProcessTracking();

                if (trackingDTO.getStatusCode() != null) {
                    processTracking.setStatusCode(trackingDTO.getStatusCode());
                }

                if (trackingDTO.getProcessStatus() != null) {
                    processTracking.setProcessStatus(trackingDTO.getProcessStatus());
                }

                if (trackingDTO.getProcessName() != null) {
                    processTracking.setProcessName(trackingDTO.getProcessName());
                }
                log.info("DTO 에서 받은 값: StatusCode={}, ProcessStatus={}, ProcessName={}",
                        trackingDTO.getStatusCode(), trackingDTO.getProcessStatus(), trackingDTO.getProcessName());

            }

            log.info("업데이트된 ProcessTracking: {}", processTracking);


            // ✅ 기존 데이터를 업데이트 (UPDATE 수행)
            processTrackingRepository.save(processTracking);


            // ✅ Hibernate Proxy 를 제거한 DTO 변환 후 반환
            ProcessTrackingDTONam responseDTO = modelMapper.map(processTracking, ProcessTrackingDTONam.class);
            return new Message(HttpStatus.OK.value(),
                    "공정 상태 업데이트 완료!", Map.of("updatedProcessTracking", responseDTO));

        } catch (Exception e) {
            log.error("서비스 : 공정 상태 업데이트 중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(),
                    "공정 상태 업데이트 실패: " + e.getMessage(), new HashMap<>());
        }
    }


}





