package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.MaturationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingMaturationDetailsRepository extends JpaRepository<MaturationDetails, String> {
    Optional<MaturationDetails> findByLotNo(String lotNo);
}
