package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {


    @Query("SELECT defaultPermission.permission FROM RolesDefaultPermissions defaultPermission WHERE defaultPermission.role.roleId = :roleId")
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);


    @Query(value = "select * from permission where permission_name=:name", nativeQuery = true)
    Optional<Permission>findByNameIncludingDeleted(@Param("name")String name);
}
