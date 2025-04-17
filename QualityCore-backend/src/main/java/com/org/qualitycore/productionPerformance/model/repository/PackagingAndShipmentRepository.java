package com.org.qualitycore.productionPerformance.model.repository;

import com.org.qualitycore.routing.model.entity.PackagingAndShipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PackagingAndShipmentRepository extends JpaRepository<PackagingAndShipment, String>,
        QuerydslPredicateExecutor<PackagingAndShipment>, PackagingAndShipmentCustomRepository {
}