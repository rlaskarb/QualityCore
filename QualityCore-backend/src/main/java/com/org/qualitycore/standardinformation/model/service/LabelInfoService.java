package com.org.qualitycore.standardinformation.model.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.org.qualitycore.productionPlan.model.entity.ProductBom;
import com.org.qualitycore.productionPlan.model.entity.QProductBom;
import com.org.qualitycore.productionPlan.model.repository.ProductBomRepository;
import com.org.qualitycore.standardinformation.model.dto.EquipmentInfoDTO;
import com.org.qualitycore.standardinformation.model.dto.LabelInfoDTO;
import com.org.qualitycore.standardinformation.model.entity.LabelInfo;
import com.org.qualitycore.standardinformation.model.entity.QLabelInfo;
import com.org.qualitycore.standardinformation.model.repository.LabelInfoRepository;
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
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LabelInfoService {

    private final JPAQueryFactory queryFactory;
    private final ModelMapper modelMapper;
    private final ProductBomRepository productBomRepository;
    private final LabelInfoRepository labelInfoRepository;
    private final Cloudinary cloudinary;

    // 전체조회
    public Page<LabelInfoDTO> findAllLabelInfo(Pageable pageable, String search) {
        QLabelInfo labelInfo = QLabelInfo.labelInfo;
        QProductBom productBomEntity = QProductBom.productBom;
        BooleanBuilder where = new BooleanBuilder();

        if (StringUtils.hasText(search)) {
            where.and(productBomEntity.productName.containsIgnoreCase(search));
        }

        long totalCount = Optional.ofNullable(queryFactory
                        .select(labelInfo.count())
                        .from(labelInfo)
                        .join(labelInfo.productBom, productBomEntity)
                        .where(where)
                        .fetchOne())
                .orElse(0L);

        List<LabelInfoDTO> labelInfoList = queryFactory
                .select(Projections.fields(LabelInfoDTO.class,
                        labelInfo.labelId.as("labelId"),
                        labelInfo.productionDate.as("productionDate"),
                        labelInfo.labelImage.as("labelImage"),
                        labelInfo.beerImage.as("beerImage"),
                        labelInfo.beerSupplier.as("beerSupplier"),
                        productBomEntity.productId.as("productId"),
                        productBomEntity.productName.as("productName"),
                        productBomEntity.sizeSpec.as("sizeSpec"),
                        productBomEntity.alcPercent.floatValue().as("alcPercent")
                ))
                .from(labelInfo)
                .join(labelInfo.productBom, productBomEntity)
                .where(where)
                .orderBy(labelInfo.labelId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(labelInfoList, pageable, totalCount);
    }



    // 상세조회
    public LabelInfoDTO findByIdLabelInfo(String labelId) {
        QLabelInfo labelInfo = QLabelInfo.labelInfo;
        QProductBom productBomEntity = QProductBom.productBom;

        LabelInfoDTO labelInfoDTO = queryFactory
                .select(Projections.fields(LabelInfoDTO.class,
                        labelInfo.labelId.as("labelId"),
                        labelInfo.productionDate.as("productionDate"),
                        labelInfo.labelImage.as("labelImage"),
                        labelInfo.beerImage.as("beerImage"),
                        labelInfo.beerSupplier.as("beerSupplier"),
                        productBomEntity.productId.as("productId"),
                        productBomEntity.productName.as("productName"),
                        productBomEntity.sizeSpec.as("sizeSpec"),
                        productBomEntity.alcPercent.floatValue().as("alcPercent")

                ))
                .from(labelInfo)
                .join(productBomEntity)
                .on(labelInfo.productBom.productId.eq(productBomEntity.productId))
                .where(labelInfo.labelId.eq(labelId))
                .fetchOne();

        return labelInfoDTO;
    }

    // 라벨정보 등록
    @Transactional
    public void createLabelInfo(LabelInfoDTO labelInfoDTO, MultipartFile labelImage, MultipartFile beerImage) {
        try {

            // 1. ProductBom 조회
            ProductBom productBom = productBomRepository.findById(labelInfoDTO.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("ProductBom not found with ID: " + labelInfoDTO.getProductId()));

            // 2. LabelInfo 엔티티 생성 및 데이터 설정
            LabelInfo labelInfo = modelMapper.map(labelInfoDTO, LabelInfo.class);
            labelInfo.setProductBom(productBom); // ProductBom 설정

            // 3. 라벨 ID 생성
            String maxLabelId = labelInfoRepository.findMaxLabelId(); // 가장 큰 ID 조회
            String newLabelId = generateNewLabelId(maxLabelId);  // 새로운 ID 생성
            labelInfo.setLabelId(newLabelId); // 새로운 ID 설정

            // 3. 이미지 업로드 (Cloudinary)
            if (labelImage != null && !labelImage.isEmpty()) {
                String labelImageUrl = uploadImageToCloudinary(labelImage); // 라벨 이미지 업로드
                labelInfo.setLabelImage(labelImageUrl);
            }

            if (beerImage != null && !beerImage.isEmpty()) {
                String beerImageUrl = uploadImageToCloudinary(beerImage); // 맥주 이미지 업로드
                labelInfo.setBeerImage(beerImageUrl); // 맥주 이미지 URL 설정
            }

            // 4. 데이터베이스 저장
            labelInfoRepository.save(labelInfo);

        } catch (Exception e) {
            throw new RuntimeException("Error creating Label Info: " + e.getMessage());
        }
    }

    // auto increment 방식
    public String generateNewLabelId(String maxLabelId) {
        if (maxLabelId == null) {
            return "LA001";  // 첫 번째 ID가 LA001
        }

        // "LA" 접두사를 제외한 숫자 부분 추출
        String numericPart = maxLabelId.substring(2);  // 예: "LA123" -> "123"
        int newId = Integer.parseInt(numericPart) + 1;  // 숫자 부분에 +1

        // 3자리 숫자로 포맷
        return String.format("LA%03d", newId);
    }

    // 이미지 업로더
    private String uploadImageToCloudinary(MultipartFile image) throws IOException {
        Map<String, Object> uploadResult = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
        String imageUrl = (String) uploadResult.get("secure_url");

        if (imageUrl == null) {
            throw new IOException("Failed to retrieve image URL from Cloudinary");
        }

        return imageUrl;
    }

    // 라벨삭제
    @Transactional
    public void deleteLabelInfo(String labelId) {

        labelInfoRepository.deleteById(labelId);

        modelMapper.map(labelId, EquipmentInfoDTO.class);
    }
}
