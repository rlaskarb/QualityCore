package com.org.qualitycore.standardinformation.model.dto;

import lombok.*;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Schema(description = "작업장 정보 DTO")
public class WorkplaceDTO {

    @Schema(description = "작업장 고유 ID", example = "WO001")
    private String workplaceId;

    @Schema(description = "LINE ID", example = "LINE001")
    private String lineId;

    @Schema(description = "작업장 이름", example = "제1작업장")
    private String workplaceName;

    @Schema(description = "작업장 타입", example = "분쇄")
    private String workplaceType;

    @Schema(description = "작업장 코드", example = "W001")
    private String workplaceCode;

    @Schema(description = "작업장 상태", example = "가동 중")
    private String workplaceStatus;

    @Schema(description = "작업장 위치", example = "서울 공장 1층")
    private String workplaceLocation;

    @Schema(description = "작업 책임자", example = "김철수")
    private String managerName;

    @Schema(description = "작업량 용량 / 생산 가능량", example = "1000")
    private Integer workplaceCapacity;

    @Schema(description = "작업장 용량 단위", example = "L")
    private String workplaceCapacityUnit; // 새로운 컬럼 추가

}
