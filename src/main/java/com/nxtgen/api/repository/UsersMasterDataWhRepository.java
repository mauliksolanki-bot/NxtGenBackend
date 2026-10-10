package com.nxtgen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nxtgen.api.entity.UsersMasterDataWhEntity;

public interface UsersMasterDataWhRepository extends JpaRepository<UsersMasterDataWhEntity, Long> {

    @Query(
            value = "SELECT * FROM NXTGEN_USERS_MASTER_DATA_WH "
                    + "WHERE IS_ACTIVE = 'Y' AND CAST(ID AS CHAR) LIKE CONCAT(:query, '%') "
                    + "ORDER BY ID ASC LIMIT 20",
            nativeQuery = true
    )
    List<UsersMasterDataWhEntity> searchByIdPrefix(@Param("query") String query);
}
