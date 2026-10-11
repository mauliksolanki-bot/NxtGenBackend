package com.nxtgen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nxtgen.api.entity.SrSequenceEntity;

public interface SrSequenceRepository extends JpaRepository<SrSequenceEntity, String> {

    // The UPDATE holds the row lock until commit, so concurrent callers get distinct values.
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(value = "UPDATE NXTGEN_SR_SEQUENCE SET NEXT_VAL = NEXT_VAL + 1 WHERE SEQ_NAME = :name", nativeQuery = true)
    int increment(@Param("name") String name);

    @Query(value = "SELECT NEXT_VAL FROM NXTGEN_SR_SEQUENCE WHERE SEQ_NAME = :name", nativeQuery = true)
    Long currentValue(@Param("name") String name);
}