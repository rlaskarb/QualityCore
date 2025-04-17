package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.MashingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingMashingProcessRepositoryRouting extends JpaRepository<MashingProcess, String> {
    Optional<MashingProcess> findByLotNo(String lotNo);
}
