package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.BoilingProcess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoutingBoilingProcessRepository extends JpaRepository<BoilingProcess, String> {
    Optional<BoilingProcess> findByLotNo(String lotNo);

    List<BoilingProcess> findByLotNoContaining(String lotNo);
}
