package com.nxtgen.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SvcSubcategoryEntity;

public interface SvcSubcategoryRepository extends JpaRepository<SvcSubcategoryEntity, Long> {

    List<SvcSubcategoryEntity> findByCategoryIdAndIsActiveOrderByDisplayOrderAsc(Long categoryId, String isActive);

    Optional<SvcSubcategoryEntity> findByCategoryIdAndSlug(Long categoryId, String slug);
}
