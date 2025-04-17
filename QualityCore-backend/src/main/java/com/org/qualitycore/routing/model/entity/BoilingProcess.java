package com.org.qualitycore.routing.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Table(name = "BOILING_PROCESS")
@Entity(name = "BoilingProcessEntity")
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoilingProcess {

    @Id
    @Column(name = "BOILING_ID")
    private String boilingId;

    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "STATUS_CODE")
    private String statusCode;

    @Column(name = "BOILING_TIME")
    private Double boilingTime;

    @Column(name = "TEMPERATURE")
    private Double temperature;

    @Column(name = "INITIAL_WORT_VOLUME")
    private Double initialWortVolume;

    @Column(name = "POST_BOIL_WORT_VOLUME")
    private Double postBoilWortVolume;

    @Column(name = "BOIL_LOSS_VOLUME")
    private Double boilLossVolume;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME")
    private LocalDateTime expectedEndTime;

    @Column(name = "ACTUAL_END_TIME")
    private LocalDateTime actualEndTime;

    @Column(name = "PROCESS_STATUS")
    private String processStatus;

    @Column(name = "NOTES")
    private String notes;
}