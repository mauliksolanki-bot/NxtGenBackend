package com.nxtgen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SrApprovalEntity;

public interface SrApprovalRepository extends JpaRepository<SrApprovalEntity, Long> {

    void deleteBySrId(String srId);
}
