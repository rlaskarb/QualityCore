package com.org.qualitycore.standardinformation.model.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;



@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "당화 DTO")
public class MashingProcessDTO {

    @Schema(description = "당화공정 ID" , example = "MA001")
    private String mashingId;

    @Schema(description = "작업지시 ID" , example = "LOT2025021201")
    private String lotNo;

    @Schema(description = "당화 소요 시간" , example = "50")
    private Integer mashingTime;

    @Schema(description = "온도", example = "65")
    private String temperature;

    @Schema(description = "pH값" , example = "5.40")
    private Double phValue;

    @Schema(description = "곡물 비율", example = "1")
    private Double grainRatio;

    @Schema(description = "물 비율" , example = "4")
    private Double waterRatio;

    @Schema(description = "물 투입량", example = "3200")
    private Integer waterInputVolume;

    @Schema(description = "메모사항" , example = "작업자 : 강동원  작업완료" )
    private String notes;

    @Schema(description = "시작시간" , example = "2025-02-12T10:15:30")
    private LocalDateTime startTime;

    @Schema(description = "예상 종료 시간" , example = "2025-02-12T10:55:30")
    private LocalDateTime expectedEndTime;

    @Schema(description = "실제 종료 시간" , example = "2025-02-12T11:00:30")
    private LocalDateTime actualEndTime;

    @Schema(description = "공정 추적 DTO" )
    private ProcessTrackingDTONam processTracking;



}
