package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface roleRepo extends JpaRepository<Role, Long> {

    Optional<Role> findByRoleName(String roleName);
}
