package com.org.qualitycore.routing.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "FERMENTATION_DETAILS")
@Entity(name = "FermentationProcessEntity")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class FermentationDetails {

    //발효공정

    @Id
    @Column(name = "FERMENTATION_ID")
    private String fermentationId;

    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "STATUS_CODE")
    private String statusCode;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME")
    private LocalDateTime expectedEndTime;
}
