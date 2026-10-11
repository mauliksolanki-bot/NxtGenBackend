package com.nxtgen.api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SrCommentEntity;

public interface SrCommentRepository extends JpaRepository<SrCommentEntity, Long> {

    Optional<SrCommentEntity> findFirstBySrIdOrderByIdAsc(String srId);

    void deleteBySrId(String srId);
}
