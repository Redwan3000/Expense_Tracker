package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface permissionRepo extends JpaRepository<Permission, Long> {
    Optional<Permission> findByPermissionName(String name);


    List<Permission> findByRole_RoleId(Long roleId);
}
