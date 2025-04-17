package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.FiltrationProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FiltrationProcessRepository extends JpaRepository<FiltrationProcess, String> {

    @Query("SELECT MAX(CAST(SUBSTRING(f.filtrationId, 3) AS int)) FROM FiltrationProcess f")
    Integer findMaxFiltrationId();

    List<FiltrationProcess> findAllByLotNo(String lotNo);
}
