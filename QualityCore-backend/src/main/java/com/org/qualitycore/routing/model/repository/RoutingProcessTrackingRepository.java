package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.ProcessTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutingProcessTrackingRepository extends JpaRepository<ProcessTracking, String> {
    List<ProcessTracking> findByLotNoContainingAndProcessStatus(String lotNo, String processStatus);
    List<ProcessTracking> findByLotNoContaining(String lotNo);
    List<ProcessTracking> findByProcessStatus(String processStatus);

    Optional<ProcessTracking> findByLotNo(String lotNo);
}
