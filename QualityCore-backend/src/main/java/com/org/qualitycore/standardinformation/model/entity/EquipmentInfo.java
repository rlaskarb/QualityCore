package com.org.qualitycore.standardinformation.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "EQUIPMENT_INFO")
@Builder(toBuilder = true)
public class EquipmentInfo {

    @Id
    @Column(name = "EQUIPMENT_ID")
    private String equipmentId;

    @ManyToOne
    @JoinColumn(name = "WORKPLACE_ID")
    private Workplace workplace;

    @Column(name = "EQUIPMENT_NAME")
    private String equipmentName;

    @Column(name = "MODEL_NAME")
    private String modelName;

    @Column(name = "MANUFACTURER")
    private String manufacturer;

    @Column(name = "INSTALL_DATE")
    private Date installDate;

    @Column(name = "EQUIPMENT_STATUS")
    private String equipmentStatus;

    @Column(name = "EQUIPMENT_IMAGE")
    private String equipmentImage;

    @Column(name = "EQUIPMENT_ETC")
    private String equipmentEtc;

}
