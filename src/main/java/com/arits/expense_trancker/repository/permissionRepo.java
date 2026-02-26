package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface permissionRepo extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionName(String name);


    List<Permission> findByRole_RoleId(Long roleId);

    @Query(value = "select * from permission where permission_name=:name", nativeQuery = true)
    Optional<Permission>findByNameIncludingDeleted(@Param("name")String name);
}
