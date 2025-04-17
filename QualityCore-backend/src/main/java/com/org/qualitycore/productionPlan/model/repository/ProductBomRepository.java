package com.org.qualitycore.productionPlan.model.repository;

import aj.org.objectweb.asm.commons.Remapper;
import com.org.qualitycore.productionPlan.model.entity.ProductBom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductBomRepository extends JpaRepository<ProductBom, String> {
    Optional<ProductBom> findByProductId(String productId);

}
