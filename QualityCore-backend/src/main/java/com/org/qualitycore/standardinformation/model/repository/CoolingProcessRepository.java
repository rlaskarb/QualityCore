package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.CoolingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CoolingProcessRepository extends JpaRepository<CoolingProcess, String> {

    @Query("SELECT MAX(CAST(SUBSTRING(c.coolingId, 3) AS int)) FROM CoolingProcess c")
    Integer findMaxCoolingId();
}
