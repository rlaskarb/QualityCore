package com.org.qualitycore.routing.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "PostMaturationFiltrationProcessEntity")
@Table(name = "POST_MATURATION_FILTRATION")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class PostMaturationFiltration {

    //--숙성후 여과 공정

    @Id
    @Column(name = "MFILTRATION_ID")
    private String filtrationId;

    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "STATUS_CODE")
    private String statusCode;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "EXPECTED_END_TIME")
    private LocalDateTime expectedEndTime;


}
