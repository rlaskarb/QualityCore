package com.org.qualitycore.standardinformation.model.repository;


import com.org.qualitycore.standardinformation.model.entity.PostMaturationFiltration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PostMaturationFiltrationRepository extends JpaRepository<PostMaturationFiltration, String > {

    @Query("SELECT MAX(CAST(SUBSTRING(po.mfiltrationId, 4) AS int)) FROM PostMaturationFiltration po")
    Integer findMaxMfiltrationId();

}
