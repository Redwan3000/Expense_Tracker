package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Role;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String roleName);

    @Query(value = "select * from role where name=:name",nativeQuery = true)
    Optional<Role>findByNameIncludingDeleted(@Param("name") String name);


}
