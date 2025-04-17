package com.org.qualitycore.productionPlan.model.repository;

import com.org.qualitycore.productionPlan.model.entity.BeerRecipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeerRecipeRepository extends JpaRepository<BeerRecipe, String> {
    // 맥주 이름으로 레시피 찾기~
    List<BeerRecipe> findByBeerName(String productName);
}
