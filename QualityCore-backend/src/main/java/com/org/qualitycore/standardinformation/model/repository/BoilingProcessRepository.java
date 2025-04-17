package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.BoilingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface BoilingProcessRepository extends JpaRepository<BoilingProcess, String> {

    @Query("SELECT MAX(CAST(SUBSTRING(b.boilingId, 3) AS int)) FROM BoilingProcess b")
    Integer findMaxBoilingId();

    List<BoilingProcess> findAllByLotNo(String lotNo);

    Optional<BoilingProcess> findByLotNo(String lotNo);
}
