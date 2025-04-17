package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.FermentationDetails;

import com.org.qualitycore.work.model.entity.LineMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FermentationDetailsRepository extends JpaRepository<FermentationDetails,String> {
    @Query("SELECT MAX(CAST(SUBSTRING(f.fermentationId, 4) AS int)) FROM FermentationDetails f")
    Integer findMaxFermentationId();

    @Query("SELECT f.fermentationId FROM FermentationDetails f")
    List<String> findFermentationIds();

    // ✅ 반환 타입을 `Optional<FermentationDetails>`로 변경
    Optional<FermentationDetails> findByFermentationId(String fermentationId);


}
