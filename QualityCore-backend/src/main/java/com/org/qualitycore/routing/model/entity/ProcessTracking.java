package com.org.qualitycore.routing.model.entity;

import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "RoutingProcessTracking")
@Table(name = "PROCESS_TRACKING")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ProcessTracking {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRACKING_ID")
    private String trackingId;

    @Column(name = "LOT_NO")
    private String lotNo;

    @Column(name = "STATUS_CODE")
    private String statusCode;

    @Column(name = "PROCESS_STATUS")
    private String processStatus;

    @Column(name = "PROCESS_NAME")
    private String processName;


}
