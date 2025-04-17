package com.org.qualitycore.standardinformation.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class LabelInfoDTO {

//  label_info 테이블
    private String labelId;
    private String productId;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date productionDate;
    private String labelImage;
    private String beerImage;
    private String beerSupplier;
// product_bom 테이블
    private String productName;
    private String sizeSpec;
    private float alcPercent;
}
