package com.org.qualitycore.standardinformation.model.repository;


import com.org.qualitycore.standardinformation.model.entity.LineInformation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LineInformationRepository extends JpaRepository<LineInformation,String> {

    @Query("SELECT l FROM LineInformation l WHERE TRIM(UPPER(l.lineId)) = UPPER(:lineId)")
    Optional<LineInformation> findByLineId(@Param("lineId") String lineId);

}