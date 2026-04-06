package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.dto.PermissionResponseDto;
import com.arits.expense_trancker.entity.Permission;
import com.arits.expense_trancker.entity.RolesDefaultPermissions;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
public interface RolesDefaultPermissionsRepo extends JpaRepository<RolesDefaultPermissions, Long> {


//    @Query(value = "select * from roles_default_permissions where role_id=:role_id", nativeQuery = true)
//    List<RolesDefaultPermissions> findCurrentPermissionsByRoleId(@Param("role_id") Long role_id);


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


//    @Modifying
//    @Transactional
//    @Query(value = """
//insert into roles_default_permissions (role_id, permission_id,is_deleted)
//SELECT :roleId, p.id ,false
//    FROM permission p
//    WHERE p.id IN (:validPermissionsIds)
//""",nativeQuery = true)
//    void setNewPermissions(Long roleId, Set<Long> validPermissionsIds);

    @Modifying
    @Transactional
    @Query(value = """
            INSERT INTO roles_default_permissions (role_id, permission_id, is_deleted, deleted_at)
            SELECT :roleId, p.id, false, NULL
            FROM permission p
            WHERE p.id IN (:validPermissionsIds)
            ON CONFLICT (role_id, permission_id) 
            DO UPDATE SET 
                is_deleted = false, 
                deleted_at = NULL
            """, nativeQuery = true)
    void setNewPermissions(Long roleId, Set<Long> validPermissionsIds);


    List<RolesDefaultPermissions> findByRoleId(long id);


}
