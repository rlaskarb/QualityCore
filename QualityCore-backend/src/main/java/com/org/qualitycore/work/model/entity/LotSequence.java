package com.org.qualitycore.work.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "lot_sequence")
@Getter
@NoArgsConstructor
public class LotSequence {

    @Id
    private LocalDate sequenceDate;  // PK: 날짜 (예: 2024-03-11)

    @Column(nullable = false)
    private int currentSequence = 1;  // 시작값 1

    public LotSequence(LocalDate sequenceDate) {
        this.sequenceDate = sequenceDate;
    }

    public void incrementSequence() {
        this.currentSequence++;
    }
}
