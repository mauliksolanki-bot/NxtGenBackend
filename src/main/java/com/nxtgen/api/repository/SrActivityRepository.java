package com.nxtgen.api.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.nxtgen.api.entity.SrActivityEntity;

public interface SrActivityRepository extends JpaRepository<SrActivityEntity, Long> {
    List<SrActivityEntity> findBySrIdOrderByCreatedAtDescIdDesc(String srId);
    void deleteBySrId(String srId);
}
