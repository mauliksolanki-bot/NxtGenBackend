package com.nxtgen.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.UserMasterEntity;

public interface UserMasterRepository extends JpaRepository<UserMasterEntity, Long> {

    Optional<UserMasterEntity> findByUsername(String username);

    Optional<UserMasterEntity> findByEmailAddress(String emailAddress);

    boolean existsByUsername(String username);

    boolean existsByEmailAddress(String emailAddress);
}
