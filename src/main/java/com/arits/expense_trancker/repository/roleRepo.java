package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
