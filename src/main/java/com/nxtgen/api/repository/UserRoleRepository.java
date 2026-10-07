package com.nxtgen.api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.nxtgen.api.entity.UserRoleEntity;

public interface UserRoleRepository extends JpaRepository<UserRoleEntity, Long> {

    @Query("""
            SELECT r.roleName FROM UserRoleEntity ur
            JOIN RoleEntity r ON r.id = ur.roleId
            WHERE ur.username = :username AND r.isActive = 'Y'
            """)
    List<String> findActiveRoleNamesByUsername(@Param("username") String username);
}
