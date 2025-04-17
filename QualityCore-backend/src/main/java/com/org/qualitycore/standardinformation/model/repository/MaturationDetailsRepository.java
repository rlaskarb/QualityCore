package com.org.qualitycore.standardinformation.model.repository;


import com.org.qualitycore.standardinformation.model.entity.MaturationDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaturationDetailsRepository extends JpaRepository<MaturationDetails,String> {

    @Query("SELECT MAX(CAST(SUBSTRING(m.maturationId, 4) AS int)) FROM MaturationDetails m")
    Integer findMaxMaturationId();

    @Query("SELECT m.maturationId FROM MaturationDetails m")
    List<String> findMaturationIds();


    Optional<MaturationDetails> findByMaturationId(String maturationId);
}
