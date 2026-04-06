package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepo extends JpaRepository<Permission, Long> {


    @Query(value = "select * from permission where name=:name", nativeQuery = true)
    Optional<Permission> findByNameIncludingDeleted(@Param("name") String name);


    @Query(value = "select p.id from permission p where p.id in (:permissionIds)", nativeQuery = true)
    Set<Long> getPermissionID(@Param("permissionIds") List<Long> permissionIds);

    @Query(value = "select p.id" +
            " from permission p " +
            "left join roles_default_permissions rp " +
            "on p.id= rp.permission_id " +
            "AND rp.role_id = :roleId " +
            "AND rp.is_deleted = false " +
            "where p.id in(:validPermissionIds) " +
            "and rp.permission_id is null", nativeQuery = true)
    Set<Long> getNewPermissionsToAdd(@Param("roleId") Long roleId,
                                     @Param("validPermissionIds") Set<Long> validPermissionIds);



}
