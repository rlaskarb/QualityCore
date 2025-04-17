package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.FermentationTimedLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface FermentationTimedLogRepository extends JpaRepository<FermentationTimedLog,Long> {

    List<FermentationTimedLog> findAllByFermentationDetails_FermentationId(String fermentationId);
}
