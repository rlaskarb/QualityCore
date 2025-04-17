package com.org.qualitycore.productionPlan.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity(name = "BeerRecipes")
@Table(name = "BEER_RECIPES")
@Getter
@Setter
@NoArgsConstructor
public class BeerRecipe {

    @Id
    private String id;

    @Column(name = "BEER_NAME")
    private String beerName;

    @Column(name = "MATERIAL_ID")
    private String materialId;

    @Column(name = "QUANTITY")
    private Double quantity;
}