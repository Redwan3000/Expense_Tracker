package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.RolesPermissionsRepsonseDto;
import com.arits.expense_trancker.entity.UsersPermissions;
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
            set is_deleted = true, deleted_at = now()
            where permission_id not in (select unnest(CAST(:newPermissions AS bigint[])))
              and user_id in (
                  select id from users 
                          where role_id = :roleId 
                        and is_deleted = false
                        and (:parentId is null or parent_id=:parentId))
            
              and is_deleted = false
              and is_blocked = false
            """, nativeQuery = true)
    void softDeleteUnwantedUsersPermissions(@Param("roleId") Long roleId,
                                            @Param("newPermissions") Long[] newPermissions,
                                            @Param("parentId") Long parentId);


    @Modifying
    @Query(value = """
            insert into users_permissions (user_id, permission_id)
            select u.id, unnest(CAST(:newPermissions as bigint[]))
            from users u
            where u.role_id = :roleId
              and u.is_deleted = false
            and (:parentId is null or u.parent_id=:parentId)
            on conflict (user_id, permission_id) 
            do update set
                is_deleted = false,
                deleted_at = null,
                updated_at = now()
            where users_permissions.is_blocked = false
            """, nativeQuery = true)
    void setNewRolePermissionsToUsers(@Param("roleId") Long roleId,
                                      @Param("newPermissions") Long[] newPermissions,
                                      @Param("parentId") Long parentId);


    @Query(value = """
            select distinct new com.arits.expense_trancker.dto.RolesPermissionsRepsonseDto(
            p.id,
            p.name,
            p.description
            ) from UsersPermissions up 
            join up.user u
            join up.permission p 
            where u.role.id= :roleId 
            and u.isDeleted= false 
            and up.isDeleted= false                        
            and (:parentId is null  or u.parent.id=:parentId or u.id= :parentId)
            """)
    List<RolesPermissionsRepsonseDto> findRolesActivePermissions(@Param("roleId") Long roleId,
                                                                 @Param("parentId") Long parentId);


    @Query(value = """
            select distinct new com.arits.expense_trancker.dto.RolesPermissionsRepsonseDto(
            p.id,
            p.name,
            p.description
            ) from UsersPermissions up 
            join up.user u
            join up.permission p 
            where u.id= :userId 
            and u.isDeleted= false 
            and up.isDeleted= false
            """)
    List<RolesPermissionsRepsonseDto> getUsersActivePermissionList(@Param("userId") Long userId);

    @Query(value = """
            select count (up.id) 
            from UsersPermissions up 
            where up.user.id = : userId 
            and up.permission.id in :permissionIds 
            """)
    int countPermissionsInUser(@Param("userId") Long userId, @Param("permissionIds") Set<Long> permissionIds);


    @Query(value = """
            update UsersPermissions 
            set isDeleted=true , deletedAt=CURRENT_TIMESTAMP 
            where user.id=:userId and permission.id in :permissionIds
            """)
    void softDeleteUsersPermissions(@Param("userId") Long userId, @Param("permissionIds") Set<Long> permissionIds);
}
