package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.MashingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MashingProcessRepository extends JpaRepository<MashingProcess,String> {

    @Query("SELECT MAX(CAST(SUBSTRING(m.mashingId, 3) AS int)) FROM MashingProcess m")
    Integer findMaxMashingId();
}
