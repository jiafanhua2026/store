package com.nvzhuang.repository;

import com.nvzhuang.entity.ViewRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ViewRecordRepository extends JpaRepository<ViewRecord, Long> {
    @Query("SELECT COUNT(v) FROM ViewRecord v WHERE v.createdAt BETWEEN :start AND :end")
    Long countByDateRange(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    @Query("SELECT COUNT(v) FROM ViewRecord v WHERE v.productId = :productId AND v.createdAt BETWEEN :start AND :end")
    Long countByProductAndDateRange(@Param("productId") Long productId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
}