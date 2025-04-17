package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.CarbonationProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingCarbonationProcessRepository extends JpaRepository<CarbonationProcess, String> {
    Optional<CarbonationProcess> findByLotNo(String lotNo);
}
