package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.FiltrationProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutingFiltrationProcessRepository extends JpaRepository<FiltrationProcess, String> {
    Optional<FiltrationProcess> findByLotNo(String lotNo);

    List<FiltrationProcess> findByLotNoContaining(String lotNo);
}
