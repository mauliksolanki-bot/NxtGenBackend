package com.nxtgen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SrLookupEntity;

public interface SrLookupRepository extends JpaRepository<SrLookupEntity, Long> {

    List<SrLookupEntity> findByLookupTypeAndIsActiveOrderByDisplayOrderAsc(String lookupType, String isActive);
}
