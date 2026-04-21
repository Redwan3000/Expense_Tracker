package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.PermissionResponseDto;
import com.arits.expense_trancker.entity.RolesDefaultPermissions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RolesDefaultPermissionsRepo extends JpaRepository<RolesDefaultPermissions, Long> {



    @Query(value = "select " +
            "p.id as id, " +
            "p.name as name, " +
            "p.description as description " +
            "from roles_default_permissions r " +
            "join permission p on r.permission_id = p.id " +
            "where r.role_id = :roleId " +
            "and r.is_deleted = false " +
            "and p.is_deleted = false", nativeQuery = true)
    List<PermissionResponseDto> findRolesPermission(@Param("roleId") Long roleId);


    @Modifying
    @Transactional
    @Query(value = """
            update roles_default_permissions 
            set is_deleted = true, 
                deleted_at = NOW() 
            where role_id = :roleId 
              and permission_id not in (:validPermissionsIds)
              and is_deleted = false
            """, nativeQuery = true)
    void softdeleteOldPermissions(
            @Param("roleId") Long roleId,
            @Param("validPermissionsIds") Set<Long> validPermissionsIds
    );




    List<RolesDefaultPermissions> findByRoleId(long id);


@Modifying
    @Query(value = """
            update roles_default_permissions
            set is_deleted= true , deleted_at= now()
            where role_id=:roleId 
              and permission_id not in :newPermissions
              and is_deleted=false
            """,nativeQuery = true)
    void softDeleteUnwantedPermissions(@Param("roleId") Long roleId,
                                       @Param("newPermissions") Set<Long> newPermissions);

    @Modifying
    @Query(value = """
        insert into roles_default_permissions (role_id, permission_id)
        select :roleId, unnest(CAST(:newPermissions as bigint[]))
        on conflict (role_id, permission_id)
        do update set 
            is_deleted  = false,
            deleted_at  = null,
            updated_at  = now()
        """, nativeQuery = true)
    void setNewPermissions(@Param("roleId") Long roleId,
                           @Param("newPermissions") Long[] newPermissions);
}
