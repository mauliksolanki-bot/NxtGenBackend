package com.nxtgen.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SvcCategoryEntity;

public interface SvcCategoryRepository extends JpaRepository<SvcCategoryEntity, Long> {

    List<SvcCategoryEntity> findByIsActiveOrderByDisplayOrderAsc(String isActive);

    Optional<SvcCategoryEntity> findBySlug(String slug);
}
