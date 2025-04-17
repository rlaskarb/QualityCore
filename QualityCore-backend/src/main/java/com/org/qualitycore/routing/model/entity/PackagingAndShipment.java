package com.org.qualitycore.routing.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity(name = "PackagingProcessEntity")
@Table(name = "PACKAGING_AND_SHIPMENT")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PackagingAndShipment {

    @Id
    @Column(name = "PACKAGING_ID")
    private String packagingId;

    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "STATUS_CODE")
    private String statusCode;


    @Column(name = "FILLING_STATUS")
    private String fillingStatus;

    @Column(name = "SEALING_STATUS")
    private String sealingStatus;

    @Column(name = "PACKAGING_STATUS")
    private String packagingStatus;

    @Column(name = "SHIPMENT_DATE")
    private LocalDate shipmentDate;

    @Column(name = "PRODUCT_NAME")
    private String productName;

    @Column(name = "SHIPMENT_QUANTITY")
    private Integer shipmentQuantity;

    @Column(name = "GOOD_QUANTITY")
    private Integer goodQuantity;
}
