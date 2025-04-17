package com.org.qualitycore.standardinformation.model.repository;

import com.org.qualitycore.standardinformation.model.entity.LabelInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface LabelInfoRepository extends JpaRepository<LabelInfo, String> {

    @Query("SELECT MAX(l.labelId) FROM LabelInfo l")
    String findMaxLabelId();

}
