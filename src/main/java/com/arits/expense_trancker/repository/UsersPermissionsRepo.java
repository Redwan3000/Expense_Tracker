package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.PermissionResponseDto;
import com.arits.expense_trancker.entity.UsersPermissions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UsersPermissionsRepo extends JpaRepository<UsersPermissions, Long> {


    @Modifying
    @Query(value = """

            update users_permissions 
        set is_deleted = true, deleted_at = NOW() 
        where user_id in (:userIds) 
        and permission_id not in (:rolePermissionIds)
        and is_deleted = false
        """, nativeQuery = true)
    void softDeleteUsersOldPermissions(List<Long> userIds, Set<Long> rolePermissionIds);


    @Modifying
    @Transactional
    @Query(value = """
    INSERT INTO users_permissions (user_id, permission_id, is_deleted, was_locked) 
    SELECT u.id, p.id, false, false 
    FROM users u 
    JOIN permission p ON p.id IN (:validPermissionsIds) 
    WHERE u.role_id = :roleId 
    ON CONFLICT (user_id, permission_id) 
    DO UPDATE SET 
        is_deleted = false, 
        deleted_at = null 
    WHERE users_permissions.was_locked = false
    """, nativeQuery = true)
    void setUsersPermissions(@Param("roleId") Long roleId,
                             @Param("validPermissionsIds") Set<Long> validPermissionsIds);



    @Modifying
    @Transactional
    @Query(value = """
            update users_permissions up
            set is_deleted = true, 
                deleted_at = NOW(),
                was_locked=true
            from users u
            where up.user_id = u.id              
              and u.parent_id = :parentId        
              and u.role_id = :roleId           
              and up.permission_id not in (:validPermissionsIds)
              and up.is_deleted = false         
              and up.was_locked = false         
            """, nativeQuery = true)
    void softdeleteOldPermissionsForSubusers(
            @Param("parentId") Long parentId,
            @Param("roleId") Long roleId,
            @Param("validPermissionsIds") Set<Long> validPermissionsIds
    );

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO users_permissions (user_id, permission_id, is_deleted, was_locked) 
            SELECT u.id, p.id, false, false 
            FROM users u 
            JOIN permission p ON p.id IN (:validPermissionsIds) 
            WHERE u.parent_id=:parentId 
            and u.role_id=:roleId
            ON CONFLICT (user_id, permission_id) 
            DO UPDATE SET 
                is_deleted = false, 
                deleted_at = null 
            WHERE users_permissions.was_locked = false
            """, nativeQuery = true)
    void setSubUsersPermissions(@Param("parentId") Long parentId, @Param("roleId") Long roleId ,@Param("validPermissionsIds")  Set<Long> validPermissionsIds);


    @Query(value = "select " +
            "p.id as id, " +
            "p.name as name, " +
            "p.description as description " +
            "from users_permissions up " +
            "join permission p on up.permission_id = p.id " +
            "join users u on u.id=up.user_id " +
            "where u.parent_id=:parentId " +
            "and u.role_id = :roleId " +
            "and up.is_deleted = false " +
            "and up.was_locked = false", nativeQuery = true)
    List<PermissionResponseDto> findSubUserRolesPermission(@Param("parentId") Long parentId ,@Param("roleId") Long roleId);



    @Modifying
    @Query(value = """
    update users_permissions
    set is_deleted = true, deleted_at = now()
    where permission_id not in (select unnest(CAST(:newPermissions AS bigint[])))
      and user_id in (select id from users where role_id = :roleId and is_deleted = false)
      and is_deleted = false
      and is_blocked = false
    """, nativeQuery = true)
    void softDeleteUnwantedUsersPermissions(@Param("roleId") Long roleId,
                                            @Param("newPermissions") Long[] newPermissions);


    @Modifying
    @Query(value = """
    INSERT INTO users_permissions (user_id, permission_id)
    SELECT u.id, unnest(CAST(:newPermissions AS bigint[]))
    FROM users u
    WHERE u.role_id = :roleId
      AND u.is_deleted = false
    ON CONFLICT (user_id, permission_id)
    DO UPDATE SET
        is_deleted = false,
        deleted_at = null,
        updated_at = now()
    WHERE users_permissions.is_blocked = false
    """, nativeQuery = true)
    void setNewRolePermissionsToUsers(@Param("roleId") Long roleId,
                                      @Param("newPermissions") Long[] newPermissions);
}
