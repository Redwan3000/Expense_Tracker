package com.arits.expense_trancker.repository;

import com.arits.expense_trancker.entity.Gender;
import com.arits.expense_trancker.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface genderRepo extends JpaRepository<Gender,Long> {

    Optional<Gender> findByName(String name);
}
