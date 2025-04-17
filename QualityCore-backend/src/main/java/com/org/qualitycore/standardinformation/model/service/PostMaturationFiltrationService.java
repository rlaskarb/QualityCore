package com.org.qualitycore.standardinformation.model.service;


import com.org.qualitycore.common.Message;
import com.org.qualitycore.standardinformation.model.dto.FermentationDetailsDTO;
import com.org.qualitycore.standardinformation.model.dto.LineMaterialNDTO;
import com.org.qualitycore.standardinformation.model.dto.PostMaturationFiltrationDTO;
import com.org.qualitycore.standardinformation.model.dto.ProcessTrackingDTONam;
import com.org.qualitycore.standardinformation.model.entity.BoilingProcess;
import com.org.qualitycore.standardinformation.model.entity.FermentationDetails;
import com.org.qualitycore.standardinformation.model.entity.PostMaturationFiltration;
import com.org.qualitycore.standardinformation.model.repository.PostMaturationFiltrationRepository;
import com.org.qualitycore.work.model.entity.LineMaterial;
import com.org.qualitycore.work.model.entity.WorkOrders;
import com.org.qualitycore.work.model.entity.processTracking;
import com.org.qualitycore.work.model.repository.LineMaterialRepository;
import com.org.qualitycore.work.model.repository.ProcessTrackingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostMaturationFiltrationService {

    private final PostMaturationFiltrationRepository postMaturationFiltrationRepository;
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



    // ✅ 숙성 후 여과 공정 등록
    @Transactional
    public Message createPostMaturationFiltration(PostMaturationFiltrationDTO postMaturationFiltrationDTO) {
        try {
            log.info("서비스 :숙성 후 여과 공정 등록 시작 DTO {}", postMaturationFiltrationDTO);

            // DTO 가 null 인지 체크
            if (postMaturationFiltrationDTO == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "postMaturationFiltrationDTO 가 null 임", new HashMap<>());
            }

            // ID 자동 생성
            String generatedId = generateNextMfiltrationId();
            log.info("자동으로 생성되는 ID {}", generatedId);


            // ✅ 특정 LOT_NO에 대한 자재 정보 가져오기
            List<LineMaterial> lineMaterials = lineMaterialRepository.
                    findByWorkOrders_LotNo(postMaturationFiltrationDTO.getLotNo());
            if (lineMaterials.isEmpty()) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "LOT_NO가 존재하지 않습니다.", new HashMap<>());
            }

            // ✅ ModelMapper 를 사용하여 DTO -> Entity 변환
            PostMaturationFiltration postMaturationFiltration = modelMapper
                    .map(postMaturationFiltrationDTO, PostMaturationFiltration.class);

            // ✅ ID 자동 생성
            postMaturationFiltration.setMfiltrationId(generatedId);

            // ✅ 관련 엔티티 매핑 (LOT_NO 기반으로 LineMaterial 리스트 설정)
            postMaturationFiltration.setLineMaterials(lineMaterials);

            // ✅ WorkOrders 가져오기
            WorkOrders workOrders = lineMaterials.get(0).getWorkOrders();

            // LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking = processTrackingRepository
                    .findByLotNo(postMaturationFiltrationDTO.getLotNo());
            if (processTracking == null) {
                processTracking = new processTracking();
            }

            // ✅ `processTracking`에 `WorkOrders` 설정
            processTracking.setWorkOrders(workOrders); // ✅ LOT_NO와 연결

            // ✅ ProcessTracking 에 lotNo를 직접 설정할 수 없으므로, WorkOrders 에서 가져와 사용
            processTracking.setStatusCode("SC008");
            processTracking.setProcessStatus("진행 중");
            processTracking.setProcessName("숙성 후 여과");

            // ✅ `processTracking`을 `mashingProcess`에 설정
            postMaturationFiltration.setProcessTracking(processTracking);

            // ✅ 시작 시간 설정 (DTO 값이 있으면 사용, 없으면 현재 시간)
            if (postMaturationFiltration.getStartTime() == null) {
                postMaturationFiltration.setStartTime(LocalDateTime.now());
            }

            // ✅ 예상 종료 시간 자동 계산
            if (postMaturationFiltration.getExpectedEndTime() == null
                    && postMaturationFiltration.getFiltrationTime() != null) {
                postMaturationFiltration.setExpectedEndTime(postMaturationFiltration.getStartTime()
                        .plusMinutes(postMaturationFiltration.getFiltrationTime()));
            }

            log.info("ModelMapper 변환 완료 !! {}", postMaturationFiltration);

            // ✅ DB 저장
            PostMaturationFiltration savePostMaturationFiltration = postMaturationFiltrationRepository.save(postMaturationFiltration);
            log.info("서비스 숙성 후 여과 공정 등록 완료 ! {}", savePostMaturationFiltration);

            // ✅ DTO 변환 후 반환
            PostMaturationFiltrationDTO responseDTO = modelMapper.map(savePostMaturationFiltration, PostMaturationFiltrationDTO.class);

            // ✅ lotNo가 누락되지 않도록 직접 설정
            if (savePostMaturationFiltration.getProcessTracking() != null
                    && savePostMaturationFiltration.getProcessTracking().getWorkOrders() != null) {
                responseDTO.getProcessTracking().setLotNo(
                        savePostMaturationFiltration.getProcessTracking().getWorkOrders().getLotNo()
                );
            }

            Map<String, Object> result = new HashMap<>();
            result.put("savePostMaturationFiltration", responseDTO);
            return new Message(HttpStatus.CREATED.value(), "숙성 후 여과 공정 등록 완료!", result);


        } catch (IllegalArgumentException e) {
            log.error("서비스 : 입력값 오류 발생 - 이유: {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "입력값 오류: " + e.getMessage(), new HashMap<>());


        } catch (Exception e) {
            log.error("서비스 : 발효 상세 공정 등록중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(), "숙성 후 여과 공정 등록 실패: " + e.getMessage(), new HashMap<>());
        }

    }

    // 가장 큰 "MfiltrationId" 조회 후 다음 ID 생성 하룻 있는 코드!
    public String generateNextMfiltrationId() {
        Integer maxId = postMaturationFiltrationRepository.findMaxMfiltrationId();
        int nextId = (maxId != null) ? maxId + 1 : 1;
        return String.format("FIL%03d", nextId); // "FIL001"형식!
    }



    // 숙성 후 여과 공정 탁도 ,실제 종료 시간 업데이트
    @Transactional
    public Message updatePostMaturationFiltration
    (String mfiltrationId, Double turbidity,  LocalDateTime actualEndTime) {

        log.info("서비스 : 숙성 후 여과 공정 업데이트 - ID {}, 탁도 {}, 실제 종료시간 {}",
                mfiltrationId, turbidity,  actualEndTime);


        Optional<PostMaturationFiltration> postMaturationFiltrationOptional = postMaturationFiltrationRepository.findById(mfiltrationId);
        if (postMaturationFiltrationOptional.isEmpty()) {
            log.warn("숙성 후 여과 공정을 찾을 수 없음 - ID {}", mfiltrationId);
            return new Message(404, "숙성 후 여과 공정을 찾을 수 없습니다.", null);
        }

        PostMaturationFiltration postMaturationFiltration = postMaturationFiltrationOptional.get();


        if (turbidity != null) {
            postMaturationFiltration.setTurbidity(turbidity);
        }
        if (actualEndTime != null) {
            postMaturationFiltration.setActualEndTime(actualEndTime);
        }

        postMaturationFiltrationRepository.save(postMaturationFiltration);

        Map<String, Object> result = new HashMap<>();
        result.put("mfiltrationId", mfiltrationId);
        result.put("turbidity", postMaturationFiltration.getTurbidity());
        result.put("actualEndTime", postMaturationFiltration.getActualEndTime());

        log.info("숙성 후 여과 공정 업데이트 완료 - ID {}, 결과: {}", mfiltrationId, result);

        return new Message(200, "숙성 후 여과 공정이 성공적으로 업데이트되었습니다.", result);
    }


    // 공정 상태 코드 추적 ( SC008 , 진행 중 , 숙성 후 여과 공정 업데이트)
    @jakarta.transaction.Transactional
    public Message updatePostMaturationFiltrationStatus(PostMaturationFiltrationDTO postMaturationFiltrationDTO) {
        try {
            log.info("📌 서비스: 업데이트할 processStatus={}",
                    postMaturationFiltrationDTO.getProcessTracking().getProcessStatus());

            // ✅ LOT_NO를 기반으로 기존 ProcessTracking 조회
            processTracking processTracking =
                    processTrackingRepository.findByLotNo(postMaturationFiltrationDTO.getLotNo());

            // ✅ DTO 가 null 인지 체크
            if (postMaturationFiltrationDTO == null || postMaturationFiltrationDTO.getLotNo() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "postMaturationFiltrationDTO 또는 LOT_NO가 null 입니다.", new HashMap<>());
            }

            // ✅ trackingId가 없으면 업데이트 불가
            if (processTracking.getTrackingId() == null) {
                return new Message(HttpStatus.BAD_REQUEST.value(),
                        "ProcessTracking 의 ID가 없습니다.", new HashMap<>());
            }


            log.info("DTO 에서 받은 값: StatusCode={}, ProcessStatus={}, ProcessName={}",
                    processTracking.getStatusCode(), processTracking.getProcessStatus(), processTracking.getProcessName());


            log.info("업데이트된 ProcessTracking: {}", processTracking);

            // ✅ 기존 데이터를 업데이트 (UPDATE 수행)
            processTrackingRepository.save(processTracking);

            // ✅ Hibernate Proxy 를 제거한 DTO 변환 후 반환
            ProcessTrackingDTONam responseDTO = modelMapper.map(processTracking, ProcessTrackingDTONam.class);
            return new Message(HttpStatus.OK.value(),
                    "숙성 후 여과 공정 상태 업데이트 완료!", Map.of("updatedProcessTracking", responseDTO));

        } catch (Exception e) {
            log.error("서비스 : 숙성 후 여과 공정 상태 업데이트 중 오류 발생 {}", e.getMessage(), e);
            return new Message(HttpStatus.BAD_REQUEST.value(),
                    "숙성 후 여과 공정 상태 업데이트 실패: " + e.getMessage(), new HashMap<>());
        }

    }



}
