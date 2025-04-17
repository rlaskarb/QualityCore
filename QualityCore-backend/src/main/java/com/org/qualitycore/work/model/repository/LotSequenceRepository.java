package com.org.qualitycore.work.model.repository;

import com.org.qualitycore.work.model.entity.LotSequence;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface LotSequenceRepository extends JpaRepository<LotSequence, LocalDate> {

    @Lock(LockModeType.PESSIMISTIC_WRITE) // 동시성 제어
    Optional<LotSequence> findBySequenceDate(LocalDate date);
}
