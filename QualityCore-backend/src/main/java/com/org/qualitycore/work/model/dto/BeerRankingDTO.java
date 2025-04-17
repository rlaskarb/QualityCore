package com.org.qualitycore.work.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;  // java.sql.Date 말고 java.util.Date로!

@NoArgsConstructor
@Getter
@Setter
@ToString
public class BeerRankingDTO {

    private String productName;   // 맥주 이름
    private Long totalProduction; // 총 생산량 (Integer → Long)
    private Integer lineNo;        // 생산 라인 정보
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date startDate;       // 생산 시작일 (LocalDate → Date)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private Date endDate;         // 생산 종료일 (LocalDate → Date)

    public BeerRankingDTO(String productName, Long totalProduction, Integer lineNo, Date startDate, Date endDate) {
        this.productName = productName;
        this.totalProduction = totalProduction;
        this.lineNo = lineNo;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}