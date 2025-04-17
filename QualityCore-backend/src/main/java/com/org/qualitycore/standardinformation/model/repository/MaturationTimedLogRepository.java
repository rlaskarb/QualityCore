package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.FermentationTimedLog;
import com.org.qualitycore.standardinformation.model.entity.MaturationTimedLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaturationTimedLogRepository extends JpaRepository<MaturationTimedLog,Long> {

    List<MaturationTimedLog> findAllByMaturationDetails_MaturationId(String maturationId);

    // maturationId로 검색하는 쿼리 추가
    @Query("SELECT l FROM MaturationTimedLog l WHERE l.maturationDetails.maturationId = :maturationId")
    List<MaturationTimedLog> findByMaturationId(@Param("maturationId") String maturationId);
}
