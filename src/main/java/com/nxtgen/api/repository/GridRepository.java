package com.nxtgen.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.GridEntity;

public interface GridRepository extends JpaRepository<GridEntity, Long> {

    Optional<GridEntity> findByGridName(String gridName);
}
