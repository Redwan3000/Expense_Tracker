package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.UsersPermissions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsersPermissionsRepo extends JpaRepository<UsersPermissions, Long> {

@Query(value = "select * from users_permissions where id=:id",nativeQuery = true)
    List<UsersPermissions> findPermissionsByUserId(@Param("id") long id);
}
