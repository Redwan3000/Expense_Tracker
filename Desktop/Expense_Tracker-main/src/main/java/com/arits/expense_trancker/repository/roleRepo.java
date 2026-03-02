package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Role;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Optional;

@Repository
public interface roleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);

    @Query(value = "select * from role where role_name=:name",nativeQuery = true)
Optional<Role>findByNameIncludingDeleted(@Param("name") String name);


    @Modifying
    @Transactional
    @Query(value = "UPDATE roles_permissions SET is_deleted = true " +
            "WHERE role_id = :roleId AND permission_id = :permissionId",
            nativeQuery = true)
    void softDeletePermissionFromRole(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);


    @Modifying
    @Transactional
    @Query(value = "INSERT INTO roles_permissions (role_id, permission_id, is_deleted) " +
            "VALUES (:roleId, :permissionId, false) " +
            "ON CONFLICT (role_id, permission_id) DO UPDATE SET is_deleted = false",
            nativeQuery = true)
    void addPermissionToRoleSoftAware(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);

}
