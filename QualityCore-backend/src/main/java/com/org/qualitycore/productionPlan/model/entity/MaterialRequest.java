package com.org.qualitycore.productionPlan.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "MATERIAL_REQUEST")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MaterialRequest {
    @Id
    @Column(name = "REQUEST_ID")
    private String requestId;

    @ManyToOne
    @JoinColumn(name = "PLAN_MATERIAL_ID", nullable = false)
    private PlanMaterial planMaterial;

    @Column(name = "REQUEST_QTY")
    private Double requestQty;

    @Column(name = "DELIVERY_DATE")
    private LocalDate deliveryDate;

    @Column(name = "REASON")
    private String reason;

    @Column(name = "NOTE")
    private String note;

    @Column(name = "REQUEST_DATE")
    private LocalDate requestDate = LocalDate.now();


    @Column(name = "MATERIAL_ID", nullable = true)
    private String materialId;


    @Column(name = "STATUS")
    private String status;
}
