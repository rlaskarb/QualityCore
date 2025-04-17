package com.org.qualitycore.standardinformation.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "숙성 후 여과 공정 DTO")
public class PostMaturationFiltrationDTO {

    @Schema(description = "여과 공정 고유 ID", example = "FIL001")
    private String mfiltrationId;

    @Schema(description = "작업지시 ID", example = "LOT2025030801")
    private String lotNo;

    @Schema(description = "여과 소요 시간 (분)", example = "120")
    private Double filtrationTime;

    @Schema(description = "탁도 (NTU 단위)", example = "1.5")
    private Double turbidity;

    @Schema(description = "작업 시작 시간", example = "2025-03-08T10:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @Schema(description = "예상 종료 시간", example = "2025-03-08T12:00:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expectedEndTime;

    @Schema(description = "실제 종료 시간", example = "2025-03-08T12:30:00")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime actualEndTime;

    @Schema(description = "메모 사항", example = "여과 완료, 품질 점검 필요")
    private String notes;

    @Schema(description = "공정 추적 DTO" )
    private ProcessTrackingDTONam processTracking;
}
