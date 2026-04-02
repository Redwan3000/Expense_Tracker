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


    @Query(value ="select " +
            "p.id as permissionId," +
            "p.name as permissionName," +
            "p.description as description" +
            "from permission p " +
            " left join roles_default_permissions r on p.id= r.permission_id where r.role_id=:roleId ",nativeQuery = true)
    List<Permission> findPermissionsByRoleId(@Param("roleId") Long roleId);


    @Query(value = "select * from permission where name=:name", nativeQuery = true)
    Optional<Permission>findByNameIncludingDeleted(@Param("name")String name);
}
