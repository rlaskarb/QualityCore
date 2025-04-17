package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.EquipmentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipmentInfoRepository extends JpaRepository<EquipmentInfo, String> {

    @Query("SELECT MAX(e.equipmentId) FROM EquipmentInfo e")
    String findMaxEquipmentId();
}
