package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.PackagingAndShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PPackagingAndShipmentRepository extends JpaRepository<PackagingAndShipment, String> {

    @Query("SELECT MAX(CAST(SUBSTRING(p.packagingId, 3) AS int)) FROM PackagingAndShipment p")
    Integer findMaxPackagingId();


}
