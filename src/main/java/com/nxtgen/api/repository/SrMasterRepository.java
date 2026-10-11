package com.nxtgen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SrMasterEntity;

public interface SrMasterRepository extends JpaRepository<SrMasterEntity, String> {
}
