package com.nxtgen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nxtgen.api.entity.SrApprovalCheckEntity;

public interface SrApprovalCheckRepository extends JpaRepository<SrApprovalCheckEntity, Long> {

    List<SrApprovalCheckEntity> findByRequestTypeAndIsActiveOrderByApprovalLevelAsc(String requestType, String isActive);

    @Query("select distinct c.requestType from SrApprovalCheckEntity c where c.isActive = :isActive order by c.requestType")
    List<String> findDistinctRequestTypes(@Param("isActive") String isActive);
}
