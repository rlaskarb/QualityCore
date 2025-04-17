package com.org.qualitycore.work.model.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity(name = "BeerRecipesWork")
@Table(name = "BEER_RECIPES")
public class BeerRecipesWork {

    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "BEER_NAME")
    private String beerName;

    @Column(name = "QUANTITY")
    private String quantity;

    @Column(name = "PROCESS_STEP")
    private String processStep;

    @ManyToOne
    @JoinColumn(name = "MATERIAL_ID", referencedColumnName = "MATERIAL_ID")
    private MaterialWarehouseWork material;
}
