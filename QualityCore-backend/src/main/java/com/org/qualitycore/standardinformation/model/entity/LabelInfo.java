package com.org.qualitycore.standardinformation.model.entity;

import com.org.qualitycore.productionPlan.model.entity.ProductBom;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "LABEL_INFO")
public class LabelInfo {

    @Id
    @Column(name = "LABEL_ID")
    private String labelId;

    @ManyToOne
    @JoinColumn(name = "PRODUCT_ID")
    private ProductBom productBom;

    @Column(name = "PRODUCTION_DATE")
    private Date productionDate;

    @Column(name = "LABEL_IMAGE")
    private String labelImage;

    @Column(name = "BEER_IMAGE")
    private String beerImage;

    @Column(name = "BEER_SUPPLIER")
    private String beerSupplier;
}
