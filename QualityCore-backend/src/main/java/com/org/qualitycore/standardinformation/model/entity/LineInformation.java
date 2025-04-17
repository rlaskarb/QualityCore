package com.org.qualitycore.standardinformation.model.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


@Entity
@Table(name ="LINE_INFORMATION")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Schema(description = "라인정보 엔티티")
public class LineInformation {

    @Id
    @Column(name = "LINE_ID", nullable=false,updatable = false)
    @Schema(description = "LINE ID" , example = "LINE001")
    private String lineId; // 라인 아이디

    @Column(name = "LINE_NAME", nullable = false)
    @Schema(description = "라인 이름", example = "제1생산라인")
    private String lineName;


}
