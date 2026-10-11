package com.nxtgen.api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nxtgen.api.entity.UserAuditEntity;

public interface UserAuditRepository extends JpaRepository<UserAuditEntity, Long> {

    interface LastActivity {
        String getUsername();

        LocalDateTime getLastDate();
    }

    @Query("select a.username as username, max(a.creDate) as lastDate "
            + "from UserAuditEntity a where a.activity = :activity group by a.username")
    List<LastActivity> findLastActivityDates(@Param("activity") String activity);
}
