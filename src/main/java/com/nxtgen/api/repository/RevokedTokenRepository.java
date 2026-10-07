package com.nxtgen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.RevokedTokenEntity;

public interface RevokedTokenRepository extends JpaRepository<RevokedTokenEntity, Long> {

    boolean existsByTokenJti(String tokenJti);
}
