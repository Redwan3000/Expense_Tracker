package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.RolesDefaultPermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface RolesDefaultPermissionsRepo extends JpaRepository<RolesDefaultPermissions, Long> {


    @Query(value = "select * from roles_default_permissions where role_id=:role_id", nativeQuery = true)
    List<RolesDefaultPermissions> findCurrentPermissionsByRoleId(@Param("role_id") Long role_id);


}
