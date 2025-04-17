package com.org.qualitycore.routing.model.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "CoolingProcessEntity")
@Table(name = "COOLING_PROCESS")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CoolingProcess {

    //냉각공정

    @Id
    @Column(name = "COOLING_ID")
    private String coolingId;

    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "STATUS_CODE")
    private String statusCode;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME")
    private LocalDateTime expectedEndTime;
}
