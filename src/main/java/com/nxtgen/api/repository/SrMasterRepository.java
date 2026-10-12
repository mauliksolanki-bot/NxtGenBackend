package com.nxtgen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SrMasterEntity;

public interface SrMasterRepository extends JpaRepository<SrMasterEntity, String> {
    @org.springframework.data.jpa.repository.Query(value = "SELECT * FROM NXTGEN_SR_MASTER WHERE SR_ID = :srId FOR UPDATE", nativeQuery = true)
    java.util.Optional<SrMasterEntity> findForUpdate(@org.springframework.data.repository.query.Param("srId") String srId);
    java.util.List<SrMasterEntity> findByOpenedByUsernameOrderByOpenedAtDescSrIdDesc(String username);
}
