package com.org.qualitycore.routing.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "GrindingProcessEntity")
@Table(name = "MATERIAL_GRINDING")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class MaterialGrinding {

    //분쇄작업

    @Id
    @Column(name = "GRINDING_ID")
    private String grindingId;

    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "STATUS_CODE")
    private String statusCode;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME")
    private LocalDateTime expectedEndTime;

}
