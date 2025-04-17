package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.CarbonationProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CarbonationProcessRepository extends JpaRepository<CarbonationProcess, String> {

    @Query("SELECT MAX(CAST(SUBSTRING(c.carbonationId, 3) AS int)) FROM CarbonationProcess c")
    Integer findMaxCarbonationId();
}
