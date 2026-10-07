package com.nxtgen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.GridColumnEntity;

public interface GridColumnRepository extends JpaRepository<GridColumnEntity, Long> {

    List<GridColumnEntity> findByGridIdOrderByDisplayOrderAsc(Long gridId);
}
