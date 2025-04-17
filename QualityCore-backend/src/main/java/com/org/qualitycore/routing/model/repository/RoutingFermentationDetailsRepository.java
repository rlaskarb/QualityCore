package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.FermentationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingFermentationDetailsRepository extends JpaRepository<FermentationDetails, String> {
    Optional<FermentationDetails> findByLotNo(String lotNo);
}
