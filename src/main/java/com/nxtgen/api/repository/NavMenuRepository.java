package com.nxtgen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.NavMenuEntity;

public interface NavMenuRepository extends JpaRepository<NavMenuEntity, Long> {

    List<NavMenuEntity> findByIsEnabledOrderByIdAsc(String isEnabled);
}
