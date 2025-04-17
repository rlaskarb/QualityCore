package com.org.qualitycore.work.model.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "PROCESS_TRACKING")
public class processTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRACKING_ID")
    private Long trackingId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "LOT_NO", referencedColumnName = "LOT_NO")
    private WorkOrders workOrders;

    @Column(name = "STATUS_CODE")
    private String statusCode = "SC001";

    @Column(name = "PROCESS_STATUS")
    private String processStatus = "대기중";

    @Column(name = "PROCESS_NAME")
    private String processName = "분쇄 및 원재료투입";


    @PrePersist
    public void prePersist() {
        if (this.statusCode == null) {
            this.statusCode = "SC001";  // ✅ 기본값 예: 'SC000'
        }
        if (this.processStatus == null) {
            this.processStatus = "대기 중";  // ✅ 기본값 예: '대기 중'
        }
        if (this.processName == null) {
            this.processName = "분쇄 및 원재료투입";  // ✅ 기본값 예: '공정 미정'
        }
    }


    // 남규 ToString WorkOrders 제거함
    @Override
    public String toString() {
        return "processTracking{" +
                "trackingId=" + trackingId +
                ", statusCode='" + statusCode + '\'' +
                ", processStatus='" + processStatus + '\'' +
                ", processName='" + processName + '\'' +
                '}';
    }
}

