package com.org.qualitycore.work.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PlanInfoDTO {

    @Schema(description = "생산계획 PK")
    private String planId;

    @Schema(description = "생산제품 PK")
    private String planProductId;

    @Schema(description = "생산라인 PK")
    private String planLineId;

    @Schema(description = "생산계획 상태")
    private String status;

    @Schema(description = "생산제품명")
    private String productName;

    @Schema(description = "생산라인")
    private Integer lineNo;

    @Schema(description = "라인 생산수량")
    private Integer planQty;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "생산시작일")
    private Date startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    @Schema(description = "생산종료일")
    private Date endDate;

}
