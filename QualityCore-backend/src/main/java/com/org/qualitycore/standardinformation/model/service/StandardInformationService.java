package com.org.qualitycore.standardinformation.model.service;

import com.org.qualitycore.standardinformation.model.dto.WorkplaceDTO;
import com.org.qualitycore.standardinformation.model.entity.LineInformation;
import com.org.qualitycore.standardinformation.model.entity.ErpMessage;
import com.org.qualitycore.standardinformation.model.entity.Workplace;
import com.org.qualitycore.standardinformation.model.repository.LineInformationRepository;
import com.org.qualitycore.standardinformation.model.repository.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;



@Service
@RequiredArgsConstructor
@Slf4j
public class StandardInformationService {

    private final WorkplaceRepository workplaceRepository;
    private final LineInformationRepository lineInformationRepository;
    private final ModelMapper modelMapper;


    // 작업장 전체 조회
    public List<WorkplaceDTO> getAllWorkplaces() {
        log.info("서비스 : 작업장 전체 조회 시작!");
        List<Workplace> workplaces = workplaceRepository.findAll();
        List<WorkplaceDTO> workplaceDTO =workplaces.stream()
                .map(workplace -> modelMapper.map(workplace,WorkplaceDTO.class))
                .toList();
        log.info("서비스 : 작업장 전체 조회 완료! {} 개 조회" ,workplaceDTO.size());
        return workplaceDTO;
    }



    //작업장 등록
    @Transactional
    public ErpMessage createWorkplace(WorkplaceDTO workplaceDTO) {
        try {
            log.info("서비스 작업장 등록 시작 DTO{}", workplaceDTO);

            // ID 자동 생성
            String generatedId = generateNextWorkplaceId();
            workplaceDTO.setWorkplaceId(generatedId);
            log.info("서비스 생성된 ID - {} ", generatedId);

            LineInformation lineInformation =
                    lineInformationRepository.findByLineId(workplaceDTO.getLineId())
                    .orElseThrow(() -> new IllegalArgumentException
                            ("존재하지 않는 LINE_ID 입니다: " + workplaceDTO.getLineId()));

            // DTO -> 엔티티 변환
            Workplace workplace = modelMapper.map(workplaceDTO, Workplace.class);

            // FK lineId 설정
            workplace.setLineId(workplaceDTO.getLineId());
            workplace.setLineInformation(lineInformation);

            // 엔티티에 ID 설정
            workplace.setWorkplaceId(generatedId);
            log.info(" 엔티티 변환 완료 {}", workplace);

            // DB 저장
            Workplace saveWorkplace = workplaceRepository.save(workplace);
            log.info("서비스 작업장 등록 완료 {}", saveWorkplace);

            return new ErpMessage(HttpStatus.CREATED.value(), "작업장 등록 완료");
        } catch (DataIntegrityViolationException e){
            log.info("서비스 : 데이터 무결성 오류 발생! {} ",e.getMessage(),e);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "작업장 등록 실패: 데이터 무결성 오류");
        } catch (Exception e) {
            log.error("서비스 :작업장 등록중 오류 발생 {}", e.getMessage(), e);
            return new ErpMessage(HttpStatus.BAD_REQUEST.value(), "작업장 등록 실패!" + e.getMessage());
        }
    }

    //  가장 큰 `workplaceId` 조회 후 다음 ID 생성 (WO001, WO002 형식 유지)
    public String generateNextWorkplaceId() {
        Integer maxId = workplaceRepository.findMaxWorkplaceId();
        int nextId = (maxId != null) ? maxId + 1 : 1;
        return String.format("WO%03d", nextId);  // "WO001" 형식
    }



    // 작업장 등록 수정하기
    @Transactional
    public ErpMessage updateWorkplace(String workplaceId, WorkplaceDTO workplaceDTO) {
        try {
            log.info("서비스: 작업장 정보 업데이트 시작 - ID: {}", workplaceId);

            //  기존 엔티티 조회
            Workplace workplace = workplaceRepository.findById(workplaceId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "작업장 ID를 찾을 수 없습니다. ID: " + workplaceId));

            // ModelMapper 를 사용하여 DTO 를 엔티티로 변환 (null 값은 덮어쓰지 않음)
            modelMapper.getConfiguration().setSkipNullEnabled(true);
            modelMapper.map(workplaceDTO, workplace);

            // DB 저장 (기존 엔티티 수정)
            Workplace updatedWorkplace = workplaceRepository.save(workplace);
            log.info("서비스: 작업장 정보 업데이트 완료 {}", updatedWorkplace);

            return new ErpMessage(HttpStatus.OK.value(),
                    "작업장 업데이트 완료. ID: " + updatedWorkplace.getWorkplaceId());

        } catch (Exception e) {
            log.error(" 서비스 : 작업장 업데이트 중 오류 발생 {}", e.getMessage(), e);

            return new ErpMessage(HttpStatus.BAD_REQUEST.value(),
                    "작업장 업데이트 실패!" + e.getMessage());
        }
    }




    // 작업장 등록 삭제

    @Transactional
    public ErpMessage deleteWorkplace(String workplaceId) {
        log.info("서비스 : 작업장 삭제 시작 - ID {} ", workplaceId);

        Workplace workplace = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> {
                    log.warn("서비스: 삭제할 작업장이 없음 ID {}", workplaceId);
                    return new IllegalArgumentException("해당 작업장이 존재하지 않습니다 ID" + workplaceId);
                });
        workplaceRepository.delete(workplace);
        log.info("서비스 : 작업장 삭제 완료 ID {}", workplaceId);

        return new ErpMessage(HttpStatus.OK.value(), "작업장 삭제 완료!! ID :" + workplaceId);
    }
}
