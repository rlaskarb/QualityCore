package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.CoolingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingCoolingProcessRepository extends JpaRepository<CoolingProcess, String> {
    Optional<CoolingProcess> findByLotNo(String lotNo);
}
