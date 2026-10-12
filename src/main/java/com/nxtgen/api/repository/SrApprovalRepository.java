package com.nxtgen.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nxtgen.api.entity.SrApprovalEntity;

public interface SrApprovalRepository extends JpaRepository<SrApprovalEntity, Long> {
    @org.springframework.data.jpa.repository.Query("""
        select a from SrApprovalEntity a
        where a.approverGroupId is null and (a.approverUserId = :userId
           or (a.approverUserId is null and upper(trim(a.approverRole)) <> 'MANAGER' and upper(trim(a.approverRole)) in :roles))
        order by a.createdAt desc, a.id desc
        """)
    java.util.List<SrApprovalEntity> findAssignedApprovals(@org.springframework.data.repository.query.Param("userId") Long userId,
            @org.springframework.data.repository.query.Param("roles") java.util.Collection<String> roles);

    java.util.List<SrApprovalEntity> findByApproverGroupIdInOrderByCreatedAtDescIdDesc(java.util.Collection<Long> groupIds);
    java.util.List<SrApprovalEntity> findBySrIdOrderByApprovalLevelAscIdAsc(String srId);
    java.util.List<SrApprovalEntity> findBySrIdInOrderByApprovalLevelAsc(java.util.Collection<String> srIds);

    void deleteBySrId(String srId);
}
