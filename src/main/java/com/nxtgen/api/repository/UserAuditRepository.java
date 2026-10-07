package com.nxtgen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.UserAuditEntity;

public interface UserAuditRepository extends JpaRepository<UserAuditEntity, Long> {
}
