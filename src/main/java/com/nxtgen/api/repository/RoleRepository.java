package com.nxtgen.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.RoleEntity;

public interface RoleRepository extends JpaRepository<RoleEntity, Long> {

    List<RoleEntity> findByIsActiveOrderByRoleNameAsc(String isActive);

    Optional<RoleEntity> findByIdAndIsActive(Long id, String isActive);
}
