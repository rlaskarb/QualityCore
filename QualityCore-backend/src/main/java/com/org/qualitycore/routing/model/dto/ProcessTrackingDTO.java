package com.org.qualitycore.routing.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProcessTrackingDTO {
    private String lotNo;
    private String statusCode;
    private String processStatus;
    private String processName;
    private LocalDateTime startTime;
    private LocalDateTime expectedEndTime;
}
