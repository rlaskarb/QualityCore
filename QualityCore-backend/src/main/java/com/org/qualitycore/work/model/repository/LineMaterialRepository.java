package com.org.qualitycore.work.model.repository;

import com.org.qualitycore.work.model.entity.LineMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface LineMaterialRepository extends JpaRepository<LineMaterial, String> {

    List<LineMaterial> findByWorkOrdersLotNo(String lotNo);


    // 남규 레파지토리 잘쓸께요
    // ✅ 전체 LineMaterial 데이터 조회 (정렬 추가)
    @Query("SELECT lm FROM LineMaterial lm ORDER BY lm.workOrders.lotNo ASC")
    List<LineMaterial> findAllLineMaterial();

    // 남규 레파지토리 잘쓸께요
    // ✅ WorkOrders 의 LOT_NO를 기준으로 LineMaterial 데이터 조회
    List<LineMaterial> findByWorkOrders_LotNo(String lotNo);
}
