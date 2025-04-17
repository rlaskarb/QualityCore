package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.MaterialGrinding;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingMaterialGrindingRepository extends JpaRepository<MaterialGrinding, String> {
    Optional<MaterialGrinding> findByLotNo(String lotNo);
}
