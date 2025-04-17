package com.org.qualitycore.standardinformation.model.service;

import com.org.qualitycore.standardinformation.model.dto.EquipmentInfoDTO;
import com.org.qualitycore.standardinformation.model.entity.EquipmentInfo;
import com.org.qualitycore.standardinformation.model.entity.QEquipmentInfo;
import com.org.qualitycore.standardinformation.model.entity.QWorkplace;
import com.org.qualitycore.standardinformation.model.entity.Workplace;
import com.org.qualitycore.standardinformation.model.repository.EquipmentInfoRepository;
import com.org.qualitycore.standardinformation.model.repository.WorkplaceRepository;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EquipmentInfoService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final EquipmentInfoRepository equipmentInfoRepository;
    private final WorkplaceRepository workplaceRepository;

    // 전체조회
    public Page<EquipmentInfoDTO> findEquipmentAll(Pageable pageable, String searchType, String searchKeyword) {
        QEquipmentInfo eq = QEquipmentInfo.equipmentInfo;
        QWorkplace wp = QWorkplace.workplace;
        BooleanBuilder where = new BooleanBuilder();

        // 검색 조건 추가
        if (StringUtils.hasText(searchKeyword)) {
            if ("workplaceName".equals(searchType)) {
                where.and(wp.workplaceName.containsIgnoreCase(searchKeyword));
            } else if ("workplaceType".equals(searchType)) {
                where.and(wp.workplaceType.containsIgnoreCase(searchKeyword));
            } else if ("equipmentName".equals(searchType)) {
                where.and(eq.equipmentName.containsIgnoreCase(searchKeyword));
            } else {
                // 검색 타입이 지정되지 않은 경우 모든 필드에서 검색
                where.and(eq.equipmentName.containsIgnoreCase(searchKeyword)
                        .or(wp.workplaceName.containsIgnoreCase(searchKeyword))
                        .or(wp.workplaceType.containsIgnoreCase(searchKeyword))
                        .or(eq.equipmentStatus.containsIgnoreCase(searchKeyword)));
            }
        }

        // 전체 데이터 개수 조회 (필터링 적용)
        long totalCount = Optional.ofNullable(queryFactory
                        .select(eq.count())
                        .from(eq)
                        .join(eq.workplace, wp)
                        .where(where)
                        .fetchOne())
                .orElse(0L);

        // 페이지네이션을 적용하여 데이터 조회 (필터링 적용)
        List<EquipmentInfoDTO> equipmentList = queryFactory
                .select(Projections.fields(EquipmentInfoDTO.class,
                        eq.equipmentId.as("equipmentId"),
                        eq.equipmentName.as("equipmentName"),
                        eq.modelName.as("modelName"),
                        eq.manufacturer.as("manufacturer"),
                        eq.installDate.as("installDate"),
                        eq.equipmentStatus.as("equipmentStatus"),
                        eq.equipmentImage.as("equipmentImage"),
                        eq.equipmentEtc.as("equipmentEtc"),
                        wp.workplaceId.as("workplaceId"),
                        wp.workplaceName.as("workplaceName"),
                        wp.workplaceType.as("workplaceType")
                ))
                .from(eq)
                .join(eq.workplace, wp)
                .where(where)
                .orderBy(eq.equipmentId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체로 변환하여 반환
        return new PageImpl<>(equipmentList, pageable, totalCount);
    }

    // 상세조회
    public EquipmentInfoDTO findByCodeEquipment(String equipmentId) {
        QEquipmentInfo eq = QEquipmentInfo.equipmentInfo; // EQUIPMENT_INFO 테이블의 QueryDSL 객체
        QWorkplace wp = QWorkplace.workplace; // WORKPLACE 테이블의 QueryDSL 객체

        return queryFactory
                .select(Projections.fields(EquipmentInfoDTO.class,
                        eq.equipmentId.as("equipmentId"),
                        eq.equipmentName.as("equipmentName"),
                        eq.modelName.as("modelName"),
                        eq.manufacturer.as("manufacturer"),
                        eq.installDate.as("installDate"),
                        eq.equipmentStatus.as("equipmentStatus"),
                        eq.equipmentImage.as("equipmentImage"),
                        eq.equipmentEtc.as("equipmentEtc"),
                        wp.workplaceId.as("workplaceId"),
                        wp.workplaceName.as("workplaceName"),
                        wp.workplaceType.as("workplaceType")
                ))
                .from(eq)  // EquipmentInfo 엔티티에서 시작
                .join(eq.workplace, wp)  // fetch join
                .where(eq.equipmentId.eq(equipmentId))  // 특정 equipmentId에 해당하는 정보만 조회
                .fetchOne();  // 단일 결과 반환
    }

    // 설비등록
    @Transactional
    public void createEquipment(EquipmentInfoDTO equipmentDTO) {
        // 최대 equipmentId 조회
        String maxEquipmentId = equipmentInfoRepository.findMaxEquipmentId();
        String newEquipmentId = generateNewEquipmentId(maxEquipmentId);

        // EquipmentInfo Entity 생성 및 데이터 설정
        EquipmentInfo equipmentInfo = new EquipmentInfo();
        equipmentInfo.setEquipmentId(newEquipmentId);
        equipmentInfo.setEquipmentName(equipmentDTO.getEquipmentName());
        equipmentInfo.setModelName(equipmentDTO.getModelName());
        equipmentInfo.setManufacturer(equipmentDTO.getManufacturer());
        equipmentInfo.setInstallDate(equipmentDTO.getInstallDate());
        equipmentInfo.setEquipmentStatus(equipmentDTO.getEquipmentStatus());
        equipmentInfo.setEquipmentEtc(equipmentDTO.getEquipmentEtc());

        // Workplace 찾기
        Workplace workplace = workplaceRepository.findById(equipmentDTO.getWorkplaceId())
                .orElseThrow(() -> new IllegalArgumentException("Workplace not found"));
        equipmentInfo.setWorkplace(workplace);

        // 이미 업로드된 이미지 URL을 바로 사용
        if (equipmentDTO.getEquipmentImage() != null && !equipmentDTO.getEquipmentImage().isEmpty()) {
            equipmentInfo.setEquipmentImage(equipmentDTO.getEquipmentImage());  // 업로드된 이미지 URL을 EquipmentInfo에 설정
        }

        // 데이터 저장
        equipmentInfoRepository.save(equipmentInfo);
    }

    // auto increment 방식
    private String generateNewEquipmentId(String maxEquipmentId) {
        if (maxEquipmentId == null) {
            return "EQ001";  // 첫 번째 ID가 EQ001
        }

        // "EQ" 접두사를 제외한 숫자 부분 추출
        String numericPart = maxEquipmentId.substring(2);  // 예: "EQ123" -> "123"
        int newId = Integer.parseInt(numericPart) + 1;  // 숫자 부분에 +1

        // 3자리 숫자로 포맷
        return String.format("EQ%03d", newId);
    }

    // 설비수정
    @Transactional
    public void updateEquipment(EquipmentInfoDTO equipment) {

        EquipmentInfo equipmentInfoDTO = equipmentInfoRepository.findById(equipment.getEquipmentId()).orElseThrow(IllegalArgumentException::new);

        EquipmentInfo equipmentInfo = equipmentInfoDTO.
                                      toBuilder().
                                      equipmentStatus(equipment.getEquipmentStatus()).
                                      equipmentEtc(equipment.getEquipmentEtc()).
                                      build();

        equipmentInfoRepository.save(equipmentInfo);
    }

    // 설비삭제
    @Transactional
    public void deleteEquipment(String equipmentId) {

        equipmentInfoRepository.deleteById(equipmentId);

        modelMapper.map(equipmentId, EquipmentInfoDTO.class);
    }
}
