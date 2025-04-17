package com.org.qualitycore.routing.model.repository;

import com.org.qualitycore.routing.model.entity.PostMaturationFiltration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoutingPostMaturationFiltrationRepository extends JpaRepository<PostMaturationFiltration, String> {
    Optional<PostMaturationFiltration> findByLotNo(String lotNo);
}
