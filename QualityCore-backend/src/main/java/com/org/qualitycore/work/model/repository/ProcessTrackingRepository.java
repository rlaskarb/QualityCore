package com.org.qualitycore.work.model.repository;


import com.org.qualitycore.work.model.entity.processTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessTrackingRepository extends JpaRepository<processTracking, Long> {

    // 남규 공정에서 잘쓰겠습니다 ㅋ
    @Query("SELECT p FROM processTracking p WHERE p.workOrders.lotNo = :lotNo")
    processTracking findByLotNo(String lotNo);
}
