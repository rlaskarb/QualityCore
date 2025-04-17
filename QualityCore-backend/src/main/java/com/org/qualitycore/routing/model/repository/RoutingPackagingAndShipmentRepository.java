package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.PackagingAndShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingPackagingAndShipmentRepository extends JpaRepository<PackagingAndShipment, String> {
    Optional<PackagingAndShipment> findByLotNo(String lotNo);
}
