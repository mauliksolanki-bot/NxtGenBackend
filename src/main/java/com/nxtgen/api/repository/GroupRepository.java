package com.nxtgen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nxtgen.api.entity.GroupEntity;

public interface GroupRepository extends JpaRepository<GroupEntity, Long> {

    boolean existsByGrpNameIgnoreCase(String grpName);

    boolean existsByGrpNameIgnoreCaseAndIdNot(String grpName, Long id);

    boolean existsByIdAndIsActive(Long id, String isActive);

    @Query(
            value = "SELECT * FROM NXTGEN_GROUPS_ND_TEAMS "
                    + "WHERE IS_ACTIVE = 'Y' AND LOWER(GRP_NAME) LIKE LOWER(CONCAT('%', :query, '%')) "
                    + "ORDER BY GRP_NAME ASC LIMIT 20",
            nativeQuery = true
    )
    List<GroupEntity> searchActiveByNameLike(@Param("query") String query);
}
