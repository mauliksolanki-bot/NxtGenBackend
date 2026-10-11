package com.nxtgen.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nxtgen.api.entity.UserMasterEntity;

public interface UserMasterRepository extends JpaRepository<UserMasterEntity, Long> {

    Optional<UserMasterEntity> findByUsername(String username);

    Optional<UserMasterEntity> findByEmailAddress(String emailAddress);

    boolean existsByUsername(String username);

    boolean existsByEmailAddress(String emailAddress);

    boolean existsByEmailAddressAndIdNot(String emailAddress, Long id);

    boolean existsBySrcUserId(Long srcUserId);

    boolean existsBySrcUserIdAndIdNot(Long srcUserId, Long id);

    @Query(value = """
            SELECT * FROM NXTGEN_USERS_MASTER
            WHERE ACTV_FLAG = 'Y'
              AND (CAST(ID AS CHAR) LIKE CONCAT(:query, '%')
                   OR LOWER(USERNAME) LIKE LOWER(CONCAT('%', :query, '%'))
                   OR LOWER(EMPL_NM) LIKE LOWER(CONCAT('%', :query, '%')))
            ORDER BY EMPL_NM ASC LIMIT 20
            """, nativeQuery = true)
    List<UserMasterEntity> searchActiveUsersByIdNameOrUsername(@Param("query") String query);

    @Query("""
            SELECT DISTINCT u FROM UserMasterEntity u
            JOIN UserRoleEntity ur ON ur.userId = u.id
            JOIN RoleEntity r ON r.id = ur.roleId
            WHERE r.roleName = :roleName AND r.isActive = 'Y' AND u.actvFlag = 'Y'
              AND LOWER(u.emplNm) LIKE LOWER(CONCAT('%', :query, '%'))
            ORDER BY u.emplNm ASC
            """)
    List<UserMasterEntity> searchActiveUsersByRoleNameAndNameLike(@Param("roleName") String roleName, @Param("query") String query);
}
